package com.ictedu.bot.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ictedu.bot.dto.request.BotFileRequest;
import com.ictedu.bot.dto.request.FeedbackRequest;
import com.ictedu.bot.dto.request.QuestionRequest;
import com.ictedu.bot.dto.request.SaveJsonRequest;
import com.ictedu.bot.dto.response.*;
import com.ictedu.bot.entity.*;
import com.ictedu.bot.exception.ResourceNotFoundException;
import com.ictedu.bot.repository.*;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BotService {
    
    private static final Logger logger = LoggerFactory.getLogger(BotService.class);
    
    private final OpenAiServiceWrapper openAiService;
    private final BotRepository botRepository;
    private final BotQuestionRepository questionRepository;
    private final BotAnswerRepository answerRepository;
    private final BotFileRepository fileRepository;
    private final BotAnswerFeedbackRepository feedbackRepository;
    
    @Value("${chatbot.data.file.path}")
    private String botDataFilePath;

    public BotResponse createChat(Long id) {
        Bot bot = botRepository.save(Bot.builder().id(id).build());
        return BotResponse.from(bot);
    }

    public BotQuestionResponse addQuestion(QuestionRequest request) {
        Bot bot = botRepository.findById(request.getBotId())
                .orElseThrow(() -> new ResourceNotFoundException("Chat not found"));
        BotQuestion question = BotQuestion.builder()
                .bot(bot)
                .content(request.getContent())
                .build();
        question = questionRepository.save(question);
        return BotQuestionResponse.from(question);
    }

    public BotAnswerResponse addAnswer(Long questionId) {
        logger.info("Attempting to add answer for question ID: {}", questionId);
        try {
            BotQuestion question = questionRepository.findById(questionId)
                    .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + questionId));
            
            // 여기서 메서드 이름을 수정합니다.
            List<BotAnswer> previousAnswers = answerRepository.findTop10ByBotOrderByCreatedTimeDesc(question.getBot());
            String generatedAnswer = openAiService.generateResponse(question.getContent());
            
            if (generatedAnswer == null || generatedAnswer.trim().isEmpty()) {
                logger.warn("Generated answer is null or empty for question ID: {}", questionId);
                throw new IllegalArgumentException("Generated answer cannot be null or empty");
            }
            
            BotAnswer answer = BotAnswer.builder()
                    .question(question)
                    .content(generatedAnswer)
                    .bot(question.getBot())
                    .build();
            
            answer = answerRepository.save(answer);
            
            logger.info("Answer added successfully for question ID: {}", questionId);
            return new BotAnswerResponse(answer);
        } catch (Exception e) {
            logger.error("Error occurred while adding answer for question ID: {}", questionId, e);
            throw new RuntimeException("Failed to add answer", e);
        }
    }

    public BotFileResponse addFile(BotFileRequest request) {
        Bot bot = botRepository.findById(request.getBotId())
                .orElseThrow(() -> new ResourceNotFoundException("Chat not found"));
        BotFile file = BotFile.builder()
                .bot(bot)
                .fileName(request.getFileName())
                .filePath(request.getFilePath())
                .jsonContent(request.getJsonContent())
                .build();
        file = fileRepository.save(file);
        return new BotFileResponse(file);
    }

    public BotResponse endChat(Long chatId) {
        Bot bot = botRepository.findById(chatId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat not found with id: " + chatId));
        bot.setEndedTime(LocalDateTime.now());
        bot = botRepository.save(bot);
        return BotResponse.from(bot);
    }

    @Transactional
    public void saveJsonFile(SaveJsonRequest request) {
        Bot bot = botRepository.findById(request.getBotId())
                .orElseThrow(() -> new ResourceNotFoundException("Chat not found"));
        
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonContent = objectMapper.writeValueAsString(request.getData());
            
            BotFile file = BotFile.builder()
                    .bot(bot)
                    .fileName("chat_data_" + bot.getBotId() + ".json")
                    .filePath(botDataFilePath)
                    .jsonContent(jsonContent)
                    .build();
            
            fileRepository.save(file);
            logger.info("JSON file saved successfully for bot ID: {}", bot.getBotId());
        } catch (IOException e) {
            logger.error("Error saving JSON file for bot ID: {}", bot.getBotId(), e);
            throw new RuntimeException("Failed to save JSON file", e);
        }
    }

    public BotAnswerFeedbackResponse addFeedback(Long answerId, boolean isLike) {
        BotAnswer answer = answerRepository.findById(answerId)
            .orElseThrow(() -> new ResourceNotFoundException("Answer not found with id: " + answerId));

        BotAnswerFeedback feedback = feedbackRepository.findByAnswer(answer)
            .orElse(new BotAnswerFeedback(answer));

        if (isLike) {
            feedback.incrementLikes();
        } else {
            feedback.incrementDislikes();
        }

        feedback = feedbackRepository.save(feedback);
        return new BotAnswerFeedbackResponse(feedback);
    }
}
package com.ictedu.bot.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ictedu.bot.dto.request.*;
import com.ictedu.bot.dto.response.*;
import com.ictedu.bot.entity.*;
import com.ictedu.bot.exception.ResourceNotFoundException;
import com.ictedu.bot.repository.*;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.fine_tuning.FineTuningJob;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.stream.Collectors;

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
    private final FineTuningJobsRepository fineTuningJobsRepository;
    
    @Value("${chatbot.data.file.path}")
    private String botDataFilePath;
    
    @Value("${spring.ai.openai.finetuned.model.name}")
    private String fineTunedModelName;
    
    @Value("${fine-tuning.training-data.path}")
    private String trainingDataPath;

    @Value("${spring.ai.openai.finetuned.model.id}")
    private String fineTunedModelId;

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
            
            String generatedAnswer = openAiService.generateResponseWithFineTunedModel(question.getContent());
            
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

    @Transactional
    public FineTuningJobs startFineTuning(String trainingFilePath) {
        String jobId = openAiService.startFineTuning(trainingFilePath);
        
        FineTuningJobs job = FineTuningJobs.builder()
            .jobId(jobId)
            .status("pending")
            .build();
        
        return fineTuningJobsRepository.save(job);
    }

    public FineTuningJobs getFineTuningStatus(String jobId) {
        FineTuningJobs job = fineTuningJobsRepository.findByJobId(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Fine-tuning job not found"));
        
        FineTuningJob updatedJob = openAiService.getFineTuneStatus(jobId);
        
        job.setStatus(updatedJob.getStatus());
        if ("succeeded".equals(updatedJob.getStatus())) {
            job.setCompletedAt(LocalDateTime.now());
        }
        
        return fineTuningJobsRepository.save(job);
    }

    public List<FineTuningJobs> listFineTuningJobs() {
        List<FineTuningJob> openAiJobs = openAiService.listFineTuningJobs();
        return openAiJobs.stream()
            .map(this::convertToEntityFineTuningJob)
            .collect(Collectors.toList());
    }

    private FineTuningJobs convertToEntityFineTuningJob(FineTuningJob openAiJob) {
        return FineTuningJobs.builder()
                .jobId(openAiJob.getId())
                .status(openAiJob.getStatus())
                .createdAt(convertToLocalDateTime(openAiJob.getCreatedAt()))
                .completedAt(convertToLocalDateTime(openAiJob.getFinishedAt()))
                .build();
    }

    private LocalDateTime convertToLocalDateTime(Long timestamp) {
        return timestamp != null 
            ? LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneId.systemDefault())
            : null;
    }

    public String prepareTrainingData(Long botId) throws IOException {
        List<BotQuestion> questions = questionRepository.findByBotId(botId);
        List<BotAnswer> answers = answerRepository.findByBotId(botId);

        String fileName = "training_data_" + botId + ".jsonl";
        File file = new File(trainingDataPath, fileName);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (int i = 0; i < questions.size(); i++) {
                String jsonLine = String.format("{\"prompt\": \"%s\", \"completion\": \"%s\"}",
                        questions.get(i).getContent().replace("\"", "\\\""),
                        answers.get(i).getContent().replace("\"", "\\\""));
                writer.write(jsonLine);
                writer.newLine();
            }
        }

        return file.getAbsolutePath();
    }

    @Transactional
    public FineTuningJobs startAndMonitorFineTuning(Long botId) throws IOException {
        String trainingFilePath = prepareTrainingData(botId);
        return startFineTuning(trainingFilePath);
    }

    public List<String> listFineTunedModels() {
        return openAiService.listFineTunedModels();
    }

    public void cancelFineTuningJob(String jobId) {
        openAiService.cancelFineTuningJob(jobId);
        FineTuningJobs job = fineTuningJobsRepository.findByJobId(jobId)
            .orElseThrow(() -> new ResourceNotFoundException("Fine-tuning job not found"));
        job.setStatus("cancelled");
        fineTuningJobsRepository.save(job);
    }
    /**
     * 상세 피드백을 추가합니다.
     * @param answerId 답변 ID
     * @param request 상세 피드백 요청
     * @return 업데이트된 피드백 응답
     */
    @Transactional
    public BotAnswerFeedbackResponse addDetailedFeedback(Long answerId, BotDetailedFeedbackRequest request) {
        BotAnswer answer = answerRepository.findById(answerId)
            .orElseThrow(() -> new ResourceNotFoundException("Answer not found with id: " + answerId));

        BotAnswerFeedback feedback = feedbackRepository.findByAnswer(answer)
            .orElse(new BotAnswerFeedback(answer));

        feedback.setRelevanceScore(request.getRelevanceScore());
        feedback.setClarityScore(request.getClarityScore());
        feedback.setComment(request.getComment());
        feedback.setUserId(request.getUserId());

        if (request.isLike()) {
            feedback.incrementLikes();
        } else {
            feedback.incrementDislikes();
        }

        feedback = feedbackRepository.save(feedback);
        return new BotAnswerFeedbackResponse(feedback);
    }

    /**
     * 특정 봇의 피드백을 분석합니다.
     * @param botId 봇 ID
     * @return 피드백 분석 결과
     */
    public FeedbackAnalysisResponse analyzeFeedback(Long botId) {
        List<BotAnswerFeedback> feedbacks = feedbackRepository.findByAnswerBotId(botId);

        DoubleSummaryStatistics relevanceStats = feedbacks.stream()
            .mapToDouble(f -> f.getRelevanceScore())
            .summaryStatistics();

        DoubleSummaryStatistics clarityStats = feedbacks.stream()
            .mapToDouble(f -> f.getClarityScore())
            .summaryStatistics();

        long totalLikes = feedbacks.stream().mapToLong(f -> f.getLikes()).sum();
        long totalDislikes = feedbacks.stream().mapToLong(f -> f.getDislikes()).sum();

        return FeedbackAnalysisResponse.builder()
            .avgRelevanceScore(relevanceStats.getAverage())
            .avgClarityScore(clarityStats.getAverage())
            .totalFeedbacks(feedbacks.size())
            .totalLikes(totalLikes)
            .totalDislikes(totalDislikes)
            .build();
    }
         
    
    public String generateBotResponse(String userInput) {
        return openAiService.generateResponseWithFineTunedModel(userInput);
    }
    
    public BotResponse endChat(Long chatId) {
        Bot bot = botRepository.findById(chatId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat not found with id: " + chatId));
        bot.setEndedTime(LocalDateTime.now());
        bot = botRepository.save(bot);
        return BotResponse.from(bot);
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
}
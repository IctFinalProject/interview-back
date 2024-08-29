package com.ictedu.bot.controller;



import com.ictedu.bot.dto.request.BotFileRequest;
import com.ictedu.bot.dto.request.QuestionRequest;
import com.ictedu.bot.dto.request.SaveJsonRequest;
import com.ictedu.bot.dto.response.BotAnswerFeedbackResponse;
import com.ictedu.bot.dto.response.BotAnswerResponse;
import com.ictedu.bot.dto.response.BotFileResponse;
import com.ictedu.bot.dto.response.BotQuestionResponse;
import com.ictedu.bot.dto.response.BotResponse;
import com.ictedu.bot.service.BotService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bot")
@RequiredArgsConstructor
public class BotController {
    private final BotService botService;

    @PostMapping("/chat")
    public BotResponse createChat(@RequestParam Long id) {
        return botService.createChat(id);
    }

    @PostMapping("/question")
    public BotQuestionResponse addQuestion(@RequestBody QuestionRequest request) {
        return botService.addQuestion(request);
    }

    @PostMapping("/answer")
    public BotAnswerResponse addAnswer(@RequestParam Long questionId) {
        return botService.addAnswer(questionId);
    }

    @PostMapping("/file")
    public BotFileResponse addFile(@RequestBody BotFileRequest request) {
        return botService.addFile(request);
    }

    @PostMapping("/chat/{chatId}/end")
    public BotResponse endChat(@PathVariable Long chatId) {
        return botService.endChat(chatId);
    }
    @PostMapping("/feedback")
    public ResponseEntity<?> addFeedback(@RequestParam Long answerId, @RequestParam boolean isLike) {
        try {
            BotAnswerFeedbackResponse response = botService.addFeedback(answerId, isLike);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error adding feedback: " + e.getMessage());
        }
    }
    @PostMapping("/save-json")
    public ResponseEntity<String> saveJsonFile(@RequestBody SaveJsonRequest request) {
        botService.saveJsonFile(request);
        return ResponseEntity.ok("JSON file saved successfully");
    }
}
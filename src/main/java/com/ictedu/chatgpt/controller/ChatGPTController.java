package com.ictedu.chatgpt.controller;

import com.ictedu.chatgpt.service.ChatGPTService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
public class ChatGPTController {

    private final ChatGPTService chatGPTService;

    public ChatGPTController(ChatGPTService chatGPTService) {
        this.chatGPTService = chatGPTService;
    }

    @PostMapping("/api/survey/chatgpt")
    public String getChatGPTResponse(@RequestBody Map<String, Object> requestData) {
        try {
            List<String> jobList = (List<String>) requestData.get("jobList");
            String userName = (String) requestData.get("userName");

            // GPT 서비스 호출
            return chatGPTService.getChatGPTResponse(jobList, userName);
        } catch (IOException e) {
            e.printStackTrace();
            return "Error occurred while processing your request: " + e.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
            return "An unexpected error occurred: " + e.getMessage();
        }
    }
}

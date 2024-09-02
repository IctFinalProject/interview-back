package com.ictedu.proofread.controller;

import java.io.IOException;
import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ictedu.proofread.service.ProofreadSelfService;

@RestController
public class ProofreadSelfController {

    private final ProofreadSelfService proofreadService;

    public ProofreadSelfController(ProofreadSelfService proofreadService) {
        this.proofreadService = proofreadService;
    }

    @PostMapping("/api/chatgpt-self")
    public String getChatGPTResponse(@RequestBody Map<String, String> requestData) {
        try {
            String text = requestData.get("text");
            return proofreadService.getChatGPTResponse(text);
        } catch (IOException e) {
            e.printStackTrace();
            return "Error occurred while processing your request: " + e.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
            return "An unexpected error occurred: " + e.getMessage();
        }
    }
}

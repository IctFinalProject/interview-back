package com.ictedu.proofread.controller;

import java.io.IOException;
import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ictedu.proofread.service.ProofreadService;

@RestController
public class ProofreadController {

    private final ProofreadService proofreadService;

    public ProofreadController(ProofreadService proofreadService) {
        this.proofreadService = proofreadService;
    }

    @PostMapping("/api/chatgpt")
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

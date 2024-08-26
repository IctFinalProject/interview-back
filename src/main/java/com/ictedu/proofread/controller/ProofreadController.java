package com.ictedu.proofread.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

import com.ictedu.proofread.service.ProofreadService;

@RestController
public class ProofreadController {

	    private final ProofreadService proofreadService;

	    public ProofreadController(ProofreadService proofreadService) {
	        this.proofreadService = proofreadService;
	    }

	    @PostMapping("/api/chatgpt")
	    public String getChatGPTResponse(@RequestBody Map<String, Object> requestData) {
	        try {
	            List<String> jobList = (List<String>) requestData.get("jobList");
	            String userName = (String) requestData.get("userName");

	            // GPT 서비스 호출
	            return proofreadService.getChatGPTResponse(jobList, userName);
	        } catch (IOException e) {
	            e.printStackTrace();
	            return "Error occurred while processing your request: " + e.getMessage();
	        } catch (Exception e) {
	            e.printStackTrace();
	            return "An unexpected error occurred: " + e.getMessage();
	        }
	    }
	}

package com.ictedu.bot.service;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class OpenAiServiceWrapper {
    
    private static final Logger logger = LoggerFactory.getLogger(OpenAiServiceWrapper.class);
    private final OpenAiService openAiService;
    private final String modelId;

    public OpenAiServiceWrapper(@Value("${spring.ai.openai.api-key}") String apiKey,
            @Value("${spring.ai.openai.model.id}") String modelId) {
        this.openAiService = new OpenAiService(apiKey);
        this.modelId = modelId;
    }

    public String generateResponse(String userInput) {
        try {
            var messages = Arrays.asList(
                new ChatMessage("system", "당신은 한국어로 대화하는 AI 면접 도우미입니다. 모든 응답은 반드시 한국어로 제공해야 합니다."),
                new ChatMessage("user", userInput)
            );

            var completionRequest = ChatCompletionRequest.builder()
                    .model(modelId)
                    .messages(messages)
                    .build();
            var response = openAiService.createChatCompletion(completionRequest);
            if (response.getChoices().isEmpty()) {
                throw new RuntimeException("OpenAI에서 응답이 생성되지 않았습니다.");
            }
            return response.getChoices().get(0).getMessage().getContent();
        } catch (Exception e) {
            logger.error("OpenAI에서 응답 생성 실패", e);
            throw new RuntimeException("OpenAI에서 응답 생성에 실패했습니다.", e);
        }
    }
}
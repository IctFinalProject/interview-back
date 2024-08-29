package com.ictedu.bot.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Value;
import com.ictedu.bot.service.OpenAiServiceWrapper;

@Configuration
public class OpenAiConfig {

    @Bean
    public OpenAiServiceWrapper openAiServiceWrapper(
            @Value("${spring.ai.openai.api-key}") String apiKey,
            @Value("${spring.ai.openai.model.id}") String modelId,
            @Value("${spring.ai.openai.finetuned.model.id}") String fineTunedModelId) {
        return new OpenAiServiceWrapper(apiKey, modelId, fineTunedModelId);
    }
}
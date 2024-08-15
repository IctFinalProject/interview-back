package com.ictedu.bot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.ictedu.bot.service.OpenAiServiceWrapper;

@Configuration
public class OpenAiConfig {

    @Value("${spring.ai.openai.api-key}")
    private String apiKey;

    @Value("${spring.ai.openai.model.id}")
    private String modelId;

    @Bean
    public OpenAiServiceWrapper openAiServiceWrapper() {
        return new OpenAiServiceWrapper(apiKey, modelId);
    }
}
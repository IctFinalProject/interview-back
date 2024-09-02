package com.ictedu.proofread.service;

import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class ExtractKeywordsService {
    @Value("${proofread.api-key}")
    private String apiKey;

    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String[] extractKeywords(String text) throws IOException {
        OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .build();

        // 프롬프트 생성
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("우리는 사용자가 입력한 텍스트에서 핵심 키워드를 추출하고자 합니다.\n");
        promptBuilder.append("다음 텍스트를 읽고 그 텍스트에서 중요한 키워드를 추출해 주세요.\n");
        promptBuilder.append("추출된 키워드는 간결하고 의미가 잘 전달되도록 작성해 주세요.\n");
        promptBuilder.append("키워드는 쉼표로 구분하여 나열해 주세요.\n");
        promptBuilder.append("반드시 추출된 키워드 단어들만 나열해주세요.\n");
        promptBuilder.append("텍스트:\n");
        promptBuilder.append(text);

        String prompt = promptBuilder.toString();

        // JSON 요청 본문 생성
        Map<String, Object> jsonBody = new HashMap<>();
        jsonBody.put("model", "gpt-3.5-turbo");

        Map<String, String> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", prompt);

        jsonBody.put("messages", new Object[]{message});
        jsonBody.put("max_tokens", 1500);

        RequestBody body = RequestBody.create(
            objectMapper.writeValueAsString(jsonBody),
            MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
            .url(API_URL)
            .header("Authorization", "Bearer " + apiKey)
            .post(body)
            .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();

            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            // JSON 파싱하여 필요한 데이터 추출
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            String content = jsonNode.get("choices").get(0).get("message").get("content").asText();

            // 결과를 쉼표로 구분된 키워드 배열로 변환
            String[] keywords = content.split(",\\s*");

            // 콘솔에 키워드 출력
            System.out.println("추출된 키워드: " + String.join(", ", keywords));

            return keywords;
        }
    }
}

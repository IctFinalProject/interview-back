package com.ictedu.proofread.service;

import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
public class ProofreadService {
	@Value("${proofread.api-key}")
    private String apiKey;

    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String getChatGPTResponse(List<String> jobList, String userName) throws IOException {
        OkHttpClient client = new OkHttpClient();

        // 프롬프트 생성
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("여기는 내가 뽑아온 직업들:\n");
        promptBuilder.append(String.join(", ", jobList));
        promptBuilder.append("\n\n1. 내가 보여준 직업들이 보이지? 다양할거야.");
        promptBuilder.append("\n\n2. 고졸,대졸,대학원졸 단어나 이 단어들과 결합되는 직업있으면 제외시켜");
        promptBuilder.append("\n3. 이중에서 너가 생각하기에 고액 연봉 3개와 저액 연봉 3개, 총 직업 6개를 선정해줘.");
        promptBuilder.append("\n4. 직업 6개를 선정했으면 그 직업을 가진 사람들이 다니는 회사를 추천해줘.");
        promptBuilder.append("\n5. 그리고 추천할 때 필수로 회사 링크랑 이름을 반드시 줘야해.");
        promptBuilder.append("\n6. 회사는 6개까지만 말해.");
        promptBuilder.append("\n7. 다시 한 번 말하지만 회사 링크는 필수다.");
        promptBuilder.append("\n7. 아래에서 말할떄 고액연봉,저액연봉 언급금지.");
        promptBuilder.append("\n9. <필수>아래에 있는 양식은 반드시 지켜야 한다.");

        promptBuilder.append("\n\n<필수>");
        promptBuilder.append("\n여기까지는 내가 설명한거고, 답변은 아래와 같은 양식으로 해야해.");
        promptBuilder.append("\n답변은 한국어로 해 ");
        promptBuilder.append("\n이 아래있는 말 외에 다른 말 절대 답변하지마.");

        promptBuilder.append("\n\n추천해드리는 직업은 다음과 같습니다.\n");
        promptBuilder.append("\n직업 이름 6개(너가 뽑은 직업들)\n");
        promptBuilder.append("\n각 직업의 회사들을 추천드리면\n");
        promptBuilder.append("\n1.직업이름: 회사이름(link)\n");
        promptBuilder.append("\n2.직업이름: 회사이름(link)\n");
        promptBuilder.append("\n3.직업이름: 회사이름(link)\n");
        promptBuilder.append("\n4.직업이름: 회사이름(link)\n");
        promptBuilder.append("\n5.직업이름: 회사이름(link)\n");
        promptBuilder.append("\n6.직업이름: 회사이름(link)\n");
        promptBuilder.append("\n입니다.");

        String prompt = promptBuilder.toString();

        // JSON 요청 본문 생성
        Map<String, Object> jsonBody = new HashMap<>();
        jsonBody.put("model", "gpt-3.5-turbo");

        Map<String, String> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", prompt);

        jsonBody.put("messages", new Object[]{message});
        jsonBody.put("max_tokens", 500);

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
            return jsonNode.get("choices").get(0).get("message").get("content").asText();
        }
    }
}
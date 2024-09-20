package com.ictedu.chatgpt.service;

import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@Service
public class ChatGPTService {

    @Value("${custom.api.key}")
    private String apiKey;

    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final Pattern DUPLICATE_PATTERN = Pattern.compile("(..).*\\1");  // 2글자 이상 중복 패턴
    private static final int MAX_RETRIES = 3;  // 재시도 최대 횟수

    public String getChatGPTResponse(List<String> jobList, String userName) throws IOException {

        // OkHttpClient 생성 시 타임아웃 설정 추가
        OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(90, TimeUnit.SECONDS)  // 연결 타임아웃: 90초
            .writeTimeout(90, TimeUnit.SECONDS)    // 쓰기 타임아웃: 90초
            .readTimeout(90, TimeUnit.SECONDS)     // 읽기 타임아웃: 90초
            .build();

        String prompt = buildPrompt(jobList);

        // JSON 요청 본문 생성
        Map<String, Object> jsonBody = createRequestBody(prompt);

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
            JsonNode choicesNode = jsonNode.get("choices");

            if (choicesNode == null || !choicesNode.isArray() || choicesNode.size() == 0) {
                throw new IOException("Invalid response from API");
            }

            String gptResponse = choicesNode.get(0).get("message").get("content").asText();

            // 중복된 회사 제거 및 부적절한 직업 이름 제거
            return removeDuplicateCompaniesAndCheck(gptResponse, client, jsonBody);
        }
    }

    private String buildPrompt(List<String> jobList) {
        StringBuilder promptBuilder = new StringBuilder();

        // 직업 목록 제시
        promptBuilder.append("다음은 내가 수집한 직업들입니다:\n");
        promptBuilder.append(String.join(", ", jobList));

        // 직업 이름의 부적절한 나눔과 수정 요구
        promptBuilder.append("\n\n1. '애널리스트'를 '애널'과 '리스트'로 나누는 것과 같은 부적절한 직업 이름이 있다면 수정하고 올바른 직업을 추천해줘.");

        // 특정 조건에 맞는 직업 제외
        promptBuilder.append("\n\n2. 다음 조건에 해당하는 직업들을 제외해줘: '중졸 이하', '고졸', '대졸', '대학원졸', '계열무관', '인문', '사회', '교육', '공학', '자연', '의학', '예체능'이라는 단어가 포함된 직업.");

        // 국내 근무 가능 직업 추천
        promptBuilder.append("\n\n3. 국내에서 근무할 수 있는 직업만 추천해줘. 외국 직업은 제외해줘.");

        // 직업 4개 선정
        promptBuilder.append("\n\n4. 총 4개의 직업을 선정해줘.");

        // 각 직업에 대한 국내 회사 4곳 추천
        promptBuilder.append("\n\n5. 선정된 직업당 국내 기업 4개씩 추천해줘. 외국 기업은 제외해줘.");

        
         // 회사 이름 중복 검사 및 특정 패턴 방지
        promptBuilder.append("\n\n6. 회사 이름의 뒤쪽에 같은 단어가 반복되지 않도록 해줘. 예를 들어, '병원', '사무소'처럼 특정 단어로 끝나는 회사가 여러 개 있다면, 그 단어가 계속 반복되지 않도록 다른 회사를 추천해줘.");
        promptBuilder.append("\n   예를 들어, '서울병원', '강남병원', '한강병원'처럼 '병원'으로 끝나는 회사는 1개만 추천해줘.");

        // 총 회사 수 16개로 제한
        promptBuilder.append("\n\n7. 총 추천 회사 수는 16개를 넘지 않도록 해줘.");

        // 각 직업에 대한 전망 요청
        promptBuilder.append("\n\n8. 추천된 각 직업에 대한 간단한 전망을 제공해줘.");

        // 조건 충족 여부 확인
        promptBuilder.append("\n\n9. 위의 조건들을 충족하는지 한 번 더 확실히 확인해줘.");

        // 양식 고지
        promptBuilder.append("\n\n<필수>\n너는 아래 양식에 맞춰서만 답변해야 해. 절대 양식을 벗어난 설명을 덧붙이거나 사족을 달지 말고, 반드시 아래 양식에만 맞춰서 답변해.");
        promptBuilder.append("\n추가 설명, 이유 또는 부가적인 조건에 대해 이야기하지 마. 네 답변은 오직 직업 이름, 회사 이름, 직업 전망에만 집중해야 해.");
        promptBuilder.append("\n절대 '이러한 조건에 맞춰 대답했다'는 식의 추가적인 설명을 하지 말고, 예시 양식 외에는 어떤 말도 하지 말아줘.");


        // 답변 양식 예시 제공
        promptBuilder.append("\n추천드리는 직업은 다음과 같습니다:\n\n");
        promptBuilder.append("직업 이름 4개:\n");

        // 각 직업과 회사 추천 목록 및 직업 전망 양식
        for (int i = 0; i < 4; i++) {
            promptBuilder.append("\n").append(i + 1).append(". 직업 이름: [회사 이름 1], [회사 이름 2], [회사 이름 3], [회사 이름 4]");
            promptBuilder.append("\n   간단한 전망: [직업 전망]");
        }
        
       

        return promptBuilder.toString();
    }

    private Map<String, Object> createRequestBody(String prompt) {
        Map<String, Object> jsonBody = new HashMap<>();
        jsonBody.put("model", "gpt-4");

        Map<String, String> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", prompt);

        jsonBody.put("messages", new Object[]{message});
        jsonBody.put("max_tokens", 1400);  // 안정성을 위해 max_tokens 값을 줄임

        return jsonBody;
    }

    private String removeDuplicateCompaniesAndCheck(String gptResponse, OkHttpClient client, Map<String, Object> jsonBody) throws IOException {
        String[] lines = gptResponse.split("\n");
        LinkedHashSet<String> uniqueCompanies = new LinkedHashSet<>();
        StringBuilder filteredResponse = new StringBuilder();

        int retries = 0;

        while (retries < MAX_RETRIES) {
            for (String line : lines) {
                if (line.contains("회사 이름:")) {
                    String[] parts = line.split(": ");
                    if (parts.length > 1) {
                        String[] companies = parts[1].split(", ");
                        StringBuilder uniqueLine = new StringBuilder(parts[0] + ": ");
                        boolean hasDuplicate = false;

                        for (String company : companies) {
                            if (uniqueCompanies.add(company)) {
                                uniqueLine.append(company).append(", ");
                            } else if (DUPLICATE_PATTERN.matcher(company).find()) {
                                hasDuplicate = true;
                                break; // 중복이 발견되면 루프 탈출
                            }
                        }

                        // 마지막 콤마와 공백 제거
                        if (uniqueLine.length() > 2) {
                            uniqueLine.setLength(uniqueLine.length() - 2);
                        }

                        if (hasDuplicate) {
                            retries++;
                            String newGptResponse = getNewCompanyRecommendations(client, jsonBody);
                            lines = newGptResponse.split("\n");  // 새 응답으로 lines 갱신
                            uniqueCompanies.clear();  // 중복된 회사를 제거하고 새로 시작
                            filteredResponse.setLength(0);  // 이전 결과 초기화
                            break;
                        }

                        filteredResponse.append(uniqueLine.toString()).append("\n");
                    }
                } else {
                    filteredResponse.append(line).append("\n");
                }
            }

            if (!filteredResponse.toString().isEmpty()) {
                break;
            }
        }

        if (retries >= MAX_RETRIES) {
            throw new IOException("Failed to remove duplicate companies after maximum retries");
        }

        return filteredResponse.toString().trim();
    }

    private String getNewCompanyRecommendations(OkHttpClient client, Map<String, Object> jsonBody) throws IOException {
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

            JsonNode jsonNode = objectMapper.readTree(responseBody);
            JsonNode choicesNode = jsonNode.get("choices");

            if (choicesNode == null || !choicesNode.isArray() || choicesNode.size() == 0) {
                throw new IOException("Invalid response from API");
            }

            return choicesNode.get(0).get("message").get("content").asText();
        }
    }
}

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
            .connectTimeout(30, TimeUnit.SECONDS)  // 연결 타임아웃: 30초
            .writeTimeout(30, TimeUnit.SECONDS)    // 쓰기 타임아웃: 30초
            .readTimeout(30, TimeUnit.SECONDS)     // 읽기 타임아웃: 30초
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

        promptBuilder.append("다음은 내가 수집한 직업들입니다:\n");
        promptBuilder.append(String.join(", ", jobList));
        promptBuilder.append("\n\n1. 위에 나열된 직업들을 확인할 수 있지? 매우 다양해.");

        promptBuilder.append("\n\n2. 직업 이름이 부적절하거나, 두 단어 이상으로 잘못 나뉘어져 있는 경우 그런 직업 이름을 제외하고 다른 직업을 추천해줘.");
        promptBuilder.append("\n   예를 들어, '애널리스트'를 '애널'과 '리스트'로 나누어 표현하는 것은 부적절해. 이런 잘못된 직업 이름이 있다면 다른 직업을 추천해줘.");

        promptBuilder.append("\n\n3. 다음 조건에 해당하는 직업들도 제외해줘: '중졸 이하', '고졸', '대졸', '대학원졸', '계열무관', '인문', '사회', '교육', '공학', '자연', '의학', '예체능'이라는 단어가 포함된 직업.");

        promptBuilder.append("\n\n4. 국내에서 근무할 수 있는 직업만 추천해줘.");

        promptBuilder.append("\n\n5. 총 6개의 직업을 선정해줘.");

        promptBuilder.append("\n\n6. 선정된 6개 직업에 종사하는 사람들이 근무할 만한 국내 회사를 각 직업당 4개씩 추천해줘. 모든 직업에 대해 반드시 국내 기업을 추천해줘. 외국 기업은 제외해줘.");

        promptBuilder.append("\n\n7. 총 추천 회사 수는 24개를 넘지 않도록 해줘.");

        promptBuilder.append("\n\n<필수>\n이제부터는 내가 설명한 내용을 바탕으로 답변해줘. 답변은 아래의 양식에 맞추어 한국어로 작성해줘.다시 한번 강조한다 아래 양식대로 해 ");

        promptBuilder.append("\n\n추천드리는 직업은 다음과 같습니다:\n");
        promptBuilder.append("\n직업 이름 6개:\n");

        promptBuilder.append("\n각 직업에 대한 회사 추천 목록:\n");

        for (int i = 0; i < jobList.size(); i++) {
            promptBuilder.append("\n").append(i + 1).append(". 직업 이름: [회사 이름 1], [회사 이름 2], [회사 이름 3], [회사 이름 4]");
        }

        return promptBuilder.toString();
    }
    private Map<String, Object> createRequestBody(String prompt) {
        Map<String, Object> jsonBody = new HashMap<>();
        jsonBody.put("model", "gpt-3.5-turbo");

        Map<String, String> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", prompt);

        jsonBody.put("messages", new Object[]{message});
        jsonBody.put("max_tokens", 2000);  // 안정성을 위해 max_tokens 값을 줄임

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

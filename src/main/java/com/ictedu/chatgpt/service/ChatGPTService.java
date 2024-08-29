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

    public String getChatGPTResponse(List<String> jobList, String userName) throws IOException {

        // OkHttpClient 생성 시 타임아웃 설정 추가
        OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)  // 연결 타임아웃: 30초
            .writeTimeout(30, TimeUnit.SECONDS)    // 쓰기 타임아웃: 30초
            .readTimeout(30, TimeUnit.SECONDS)     // 읽기 타임아웃: 30초
            .build();

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

        promptBuilder.append("\n\n8. 추천된 각 직업에 대해 간단한 전망만 추가로 설명해줘. 전망은 한두 문장으로 간략하게 말해줘.");

        promptBuilder.append("\n\n<필수>\n이제부터는 내가 설명한 내용을 바탕으로 답변해줘. 답변은 아래의 양식에 맞추어 한국어로 작성해줘. 다른 형식은 사용하지 말아줘.양식 순서대로가 반드시 출력해야하는 우선 순위야. ");

        promptBuilder.append("\n\n추천드리는 직업은 다음과 같습니다:\n");
        promptBuilder.append("\n직업 이름 6개:\n");

        promptBuilder.append("\n각 직업에 대한 회사 추천 목록과 직업에 대한 간단한 전망:\n");

        for (int i = 0; i < jobList.size(); i++) {
            promptBuilder.append("\n").append(i + 1).append(". 직업 이름: [회사 이름 1], [회사 이름 2], [회사 이름 3], [회사 이름 4]");
            promptBuilder.append("\n   - 전망: [직업에 대한 간단한 전망]");
        }

        String prompt = promptBuilder.toString();


        // JSON 요청 본문 생성
        Map<String, Object> jsonBody = new HashMap<>();
        jsonBody.put("model", "gpt-3.5-turbo");

        Map<String, String> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", prompt);

        jsonBody.put("messages", new Object[]{message});
        jsonBody.put("max_tokens", 3000);  // 더 많은 정보를 받을 수 있도록 max_tokens 값을 증가시킴

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
            String gptResponse = jsonNode.get("choices").get(0).get("message").get("content").asText();

            // 부적절한 직업 이름이 포함된 경우 처리
            if (containsInvalidJobName(gptResponse)) {
                // 다시 GPT에게 요청
                return getChatGPTResponse(jobList, userName);
            }

            // 중복된 회사 제거 및 2글자 이상 연속되는 중복 체크
            return removeDuplicateCompaniesAndCheck(gptResponse, client, jsonBody);
        }
    }

    // 부적절한 직업 이름이 포함되었는지 확인하는 메소드
    private boolean containsInvalidJobName(String gptResponse) {
        // 부적절한 직업 이름을 감지하기 위한 예시 패턴
        return gptResponse.contains("애널 리스트") || gptResponse.contains("항문 목록") || gptResponse.contains("기타 부적절한 조합");
    }

    // 중복된 회사명을 제거하고, 2글자 이상 중복 확인하는 메소드
    private String removeDuplicateCompaniesAndCheck(String gptResponse, OkHttpClient client, Map<String, Object> jsonBody) throws IOException {
        String[] lines = gptResponse.split("\n");
        LinkedHashSet<String> uniqueCompanies = new LinkedHashSet<>();
        StringBuilder filteredResponse = new StringBuilder();

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
                        // 중복이 있는 경우, 해당 직업에 대한 새로운 회사를 받아오기 위해 다시 GPT 요청
                        String newGptResponse = getNewCompanyRecommendations(client, jsonBody);
                        return removeDuplicateCompaniesAndCheck(newGptResponse, client, jsonBody); // 재귀 호출
                    }

                    filteredResponse.append(uniqueLine.toString()).append("\n");
                }
            } else {
                filteredResponse.append(line).append("\n");
            }
        }
        return filteredResponse.toString().trim();
    }

    // 중복이 발견된 경우, 새롭게 GPT로부터 회사를 받아오는 메소드
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
            return jsonNode.get("choices").get(0).get("message").get("content").asText();
        }
    }
}

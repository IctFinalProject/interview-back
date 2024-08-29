package com.ictedu.proofread.service;

import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

@Service
public class ProofreadMotivationService {
	@Value("${proofread.api-key}")
    private String apiKey;

    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String getChatGPTResponse(String text) throws IOException {
        OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)  // 연결 타임아웃 설정
            .writeTimeout(60, TimeUnit.SECONDS)    // 쓰기 타임아웃 설정
            .readTimeout(60, TimeUnit.SECONDS)     // 읽기 타임아웃 설정
            .build();

        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("우리는 웹페이지의 이용자가 이력서의 '지원동기' 파트에 작성한 텍스트를 기반으로 지원동기 첨삭을 할거야.\n");
        promptBuilder.append("이용자는 아직 회사에 입사하지 않은 상태고, 회사 입사를 위한 이력서의 지원동기란에 지원동기를 적고있는 상황이야.\n");
        promptBuilder.append("이용자에게 첨삭 결과를 보여줄 때는 반드시 존댓말을 사용하고 일정한 어투를 유지해야 해.\n");
        promptBuilder.append("텍스트를 읽고 지원동기 첨삭을 해주는 기준을 알려줄게. 그에 맞게 너가 메시지를 표시해주면 돼.\n");

        promptBuilder.append("첫번째 기준은 사용자가 작성한 텍스트가 공식적인 이력서 작성에 맞지 않는 문체인 경우야.\n");
        promptBuilder.append("비격식적인 표현이나 구어체를 사용하는 경우 수정을 해줘. 존댓말이나 격식을 갖춘 문체라면 수정하지 않아도 돼.\n");

        promptBuilder.append("두번째 기준은 명료성과 간결성이야.\n");
        promptBuilder.append("문장 구조가 두 번 이상 반복되는 중복 표현이나 불명확한 부분이 있는지 파악해줘.\n");


        promptBuilder.append("위의 두가지 기준에 따라 이용자가 작성한 텍스트가 적합하다고 판단되면, 해당 기준에 따른 첨삭 결과는 띄우지 않아도 돼.\n");

    
        promptBuilder.append("위의 기준들에 따라 사용자의 텍스트를 수정하여 첨삭 결과 메시지를 띄울 때, '▶ 첨삭 결과는 다음과 같습니다.'로 제목을 보여주고 한 줄 띄운 후 수정 결과 메시지를 띄워줘.\n");
        promptBuilder.append("반드시 수정이 완료된 사용자의 텍스트 전체 문장을 한 번에 보내줘.\n");
        promptBuilder.append("또한 사용자의 텍스트를 수정할 때는 요약을 하거나 글의 흐름을 바꾸면 안돼.\n");
        promptBuilder.append("원래의 문장 구조를 유지하되, 위의 기준에 맞지 않는 부분만 수정하는 식으로 해야 해.\n");

        promptBuilder.append("수정된 텍스트 전체 문장을 보냈다면, 다시 두 줄 띄우고 '▶ 수정 부분은 다음과 같습니다.'로 제목을 띄우고 다시 한 줄 띄운 후 수정 이유 메세지를 보여줘.\n");
        promptBuilder.append("수정 이유를 보여줄때는 - 하이푼으로 문장을 시작하고 수정 전 문장과 수정된 문장이 반드시 함께 나와야 해.\n");

        promptBuilder.append("<필수>\n");
        promptBuilder.append("위에서 언급한 3, 4, 5, 6, 7, 8, 9, 10번, 11번을 모두 지켜야 해.\n");
        promptBuilder.append("너는 첨삭 결과 외에는 아무것도 표시하면 안 돼.\n");
        promptBuilder.append("내가 너에게 주는 지시 사항이나, 너가 나한테 대답하는 내용은 절대로 첨삭 결과에 포함되면 안 돼.\n");
        promptBuilder.append("결과에는 오직 첨삭 메시지와 관련된 내용만 포함시키고, 그 외의 불필요한 텍스트나 내용은 절대로 포함시키지 마.\n");
        promptBuilder.append("사용자에게는 오직 첨삭 결과와 수정 이유만 보여줘야 해.\n");

        promptBuilder.append("자 그럼 아래 텍스트를 읽고 위의 지시사항에 맞게 첨삭 결과를 출력해줘.\n");
        promptBuilder.append(text);
        
        String prompt = promptBuilder.toString();

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

            JsonNode jsonNode = objectMapper.readTree(responseBody);
            return jsonNode.get("choices").get(0).get("message").get("content").asText();
        }
    }
}

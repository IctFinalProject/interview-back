package com.ictedu.proofread.service;

import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.HashMap;

@Service
public class ProofreadService {
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

     // 프롬프트 생성
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("우리는 지금부터 웹페이지의 사용자가 이력서의 '자기소개' 파트에 작성한 텍스트를 기반으로 자기소개 첨삭을 해줄거야. 사용자는 아직 회사에 입사하지 않은 상태고 회사 입사를 위한 이력서의 자기소개란에 자기소개를 적고있는 상황이야. 우리는 그걸 받아서 첨삭을 해주는 입장이고. 여기에는 몇가지 제한사항이 있어."
        		+ " - 너는 첨삭 결과 외에는 아무것도 표시하면 안 돼.\r\n"
        		+ " - 내가 너에게 주는 지시 사항이나, 너가 나한테 대답하는 내용은 절대로 첨삭 결과에 포함되면 안 돼.\r\n"
        		+ " - 사용자에게 첨삭 결과를 보여줄 때는 반드시 존댓말을 사용하고 일정한 어투를 유지해야해.\n");
        promptBuilder.append("텍스트를 읽고 자기소개 첨삭을 해주는 기준을 알려줄테니까 그에 맞게 너가 메시지를 표시해주면 돼. 기준은 다음과 같아 \n");
        promptBuilder.append("첫번째 기준은 사용자가 작성한 텍스트가 공식적인 이력서 작성에 맞지않는 문체이면 메세지를 출력해줘.존댓말이나 격식을 갖춘 문체라면 수정하지 않아도 돼. 비격식적인 표현이나 문체가 아닌 구어체를 사용하는 경우 수정을 해주면 되는거야. \n");
        promptBuilder.append("두번째 기준은 명료성과 간결성이야.문장구조가 두 번이상 반복되는 중복 표현이나 불명확한 부분이 있는지 파악해줘. \n");
        promptBuilder.append("첨삭결과 메세지를 띄울때에는 ▶ 첨삭 결과는 다음과 같습니다. 로 시작하고 한줄 띄고 수정결과 메세지를 띄워줘.\n");
        promptBuilder.append("다음의 중요한 내용들도 반드시 지켜야해.\n");
        promptBuilder.append(" 중요: 결과에 오직 첨삭 메시지와 관련된 내용만 포함시키고, 그 외의 불필요한 텍스트나 내용은 절대로 포함시키지 마.사용자에게는 오직 첨삭결과만 보여줘야해. \n");
        promptBuilder.append(" 중요: 사용자가 작성한 텍스트가 각 기준에 따라 판단했을때 적합하면 해당 기준에 따른 첨삭 결과는 띄우지 않아도 돼. \n");
        promptBuilder.append("중오: 최종적으로 너가 위의 기준들에 따라 사용자의 텍스트를 수정하여 완성된 전체 문장을 아래에 두 줄 정도 띄우고 보내줘야해. 반드시 수정 완성된 사용자의 텍스트 전체문장을 한번에 보내줘  \n");
        promptBuilder.append("중오: 사용자의 텍스트를 수정할때는 요약을 하거나 글의 흐름을 바꾸면 안돼. 원래의 문장 구조를 유지하되 위의 기준에 맞지않는 부분만 수정해주는 식으로 해야해 . \n");
        promptBuilder.append("중오: 수정된 텍스트 전체 문장을 보냈다면 또 두 줄 띄고 ▶ 수정 부분은 다음과 같습니다. 로 시작하는 수정 이유를 보여주는 메세지를 띄워줘야해. \n");
        promptBuilder.append("중오: 수정 전 텍스트 문장 원본을 먼저 보여주고 너가 수정한 이유를 알려주는 형식으로 수정 이유 메세지를 사용자에게 보여주면돼.\n");
        promptBuilder.append("자 그럼 아래 텍스트를 읽고 위의 지시사항에 맞게 첨삭 결과를 출력해줘. \n ");
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
            return jsonNode.get("choices").get(0).get("message").get("content").asText();
        }
    }
}
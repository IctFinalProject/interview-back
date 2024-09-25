package com.ictedu.proofread.service;

import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.HashMap;

@Service
public class ProofreadSelfService {
	@Value("${proofread.api-key}")
    private String apiKey;

    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String getChatGPTResponse(String text) throws IOException {
        OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .build();

     // 프롬프트 생성
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("우리는 웹페이지의 이용자가 이력서의 '자기소개' 파트에 작성한 텍스트를 기반으로 자기소개 첨삭을 할거야.\n");
        promptBuilder.append("이용자는 아직 회사에 입사하지 않은 상태고, 회사 입사를 위한 이력서의 자기소개란에 자기소개를 적고있는 상황이야.\n");
        promptBuilder.append("회사 입사를 위한 공식적인 자기소개서 작성이니까 이용자는 공식적이고 격식있는 문체로 텍스트를 작성하겠지.\n");
        promptBuilder.append("우리는 이 텍스트를 사용자에게 받아서 분석한 뒤 AI첨삭을 해주는 역할을 하는거야.\n");
        promptBuilder.append("이용자에게 첨삭 결과를 보여줄 때는 반드시 존댓말을 사용하고 일정한 어투를 유지해야 해.\n");
        promptBuilder.append("텍스트를 읽고 자기소개 첨삭을 해주는 기준을 알려줄게. 그에 맞게 너가 메시지를 표시해주면 돼.\n");
        promptBuilder.append("첫번째 기준은 이용자가 작성한 텍스트가 공식적인 이력서 작성에 맞지 않는 문체인 경우야.\n");
        promptBuilder.append("비격식적인 표현이나 구어체를 사용하는 경우 수정을 해줘.사용자가 자신을 표현할 때는 '나','내가'라고 적었을경우'저','제가' 로 수정해줘. 또한 이용자가 '~했어요'와 같은 '요' 로 끝나거나 ~할게, ~게 , ~네 등으로 문장이 끝나는 구어체를 사용자가 작성했다면 '~했습니다,~입니다' 와 같이 '~다'로 끝나는 문체로 수정해줘.\n");
        promptBuilder.append("두번째 기준은 명료성과 간결성이야.비슷한 단어나 문장이 계속 사용되거나 문장이 완전하게 끝나지 않은 문장이 있는지 파악해주고 있다면 문장을 간결하고 명확하게 끝나게 수정해주면 돼.\n");
        promptBuilder.append("문장이 완전하게 끝나지 않은 문장의 예시로는 '은,는,이,가' 등으로 문장이 불완전하게 끝나는 경우가 있겠지. 또한 명사로 문장이 끝나버리는 경우에도 완전한 문장으로 수정해줘.\n");
        promptBuilder.append("위의 기준들에 따라 사용자의 텍스트를 수정하여 첨삭 결과 메시지를 띄울 때, '▶ 첨삭 결과는 다음과 같습니다.'로 제목을 보여주고 밑에 수정 결과 메시지를 띄워줘.\n");
        promptBuilder.append("반드시 수정이 완료된 사용자의 텍스트 전체 문장을 한 번에 보내줘.\n");
        promptBuilder.append("또한 사용자의 텍스트를 수정할 때는 요약을 하거나 글의 흐름을 바꾸면 안돼.\n");
        promptBuilder.append("원래의 문장 구조를 유지하되, 위의 기준에 맞지 않는 부분만 수정하는 식으로 해야 해.\n");

        promptBuilder.append("수정된 텍스트 전체 문장을 보냈다면, 다시 두 줄 띄우고 '▶ 수정 부분은 다음과 같습니다.'로 제목을 보여주고 밑에 수정 이유 메세지를 보여줘.\n");
        promptBuilder.append("수정 이유를 보여줄때는 - 하이푼으로 틀을 시작하고 '수정이유' : '수정 전 문장' → '수정 후 문장' 이런 형식이 하나의 틀이라고 생각하면돼. 하나의 틀에는 하나의 반드시 하나의 하이푼만 들어가야해. 따라서 반드시 '수정이유' 앞에만 하이푼이 붙어야겠지. '수정이유'가 틀의 시작이니까.\n");
        
        promptBuilder.append("위의 틀에서 '수정 이유' 에는 너가 수정을 한 이유가 들어가야하고 '수정 전 문장'에는 수정을 거치기 전 사용자의 텍스트 원본만 들어가야해. '수정 후 문장'은 너가 수정을 완료한 문장만 들어가야해. \n");
        promptBuilder.append("수정이유 틀인 '수정 전 문장' 과 '수정 후 문장' 이 텍스트는 포함시키지 마.이건 내가 너에게 알려주는 틀일뿐이야. 저 틀안에 내가 요청한 문장만 사용자에게 보여주면돼.  \n");
        promptBuilder.append("너가 이해하기 쉽게 수정이유 예시를 보여주자면 다음과같아.  \n");
        promptBuilder.append(" - 수정이유 : 문체가 비격식적인 표현을 포함하고 있어 격식 있는 문체로 수정했습니다.\r\n"
        		+ "'대학교에서 여러 가지 프로젝트를 진행했습니더.' → '대학교에서 여러 가지 프로젝트를 진행했습니다.'\n");
        promptBuilder.append("위의 예시를 참고해서 같은 틀과 형식으로 수정이유를 보여주면돼. \n");
        promptBuilder.append("<필수>\n");
        promptBuilder.append("위에서 언급한 내용들을 모두 반드시 지켜야 해.\n");
        promptBuilder.append("그리고 너는 첨삭 결과 외에는 아무것도 표시하면 안 돼.\n");
        promptBuilder.append("너가 수정한 부분들은 하나도 빠뜨리지 않고 반드시 모두 수정이유 메시지로 사용자에게 보여줘야해.\n");
        promptBuilder.append("내가 너에게 주는 지시 사항이나, 너가 나한테 대답하는 내용은 절대로 첨삭 결과에 포함되면 안 돼.\n");
        promptBuilder.append("결과에는 오직 첨삭 메시지와 관련된 내용만 포함시키고, 그 외의 불필요한 텍스트나 내용은 절대로 포함시키지 마.\n");
        promptBuilder.append("사용자에게는 오직 첨삭 결과와 수정 이유만 보여줘야 해.\n");
        promptBuilder.append("자 그럼 아래 텍스트를 읽고 위의 지시사항에 맞게 첨삭 결과를 출력해줘.\n");
        promptBuilder.append(text);
        promptBuilder.append("텍스트를 분석할때는 반드시 원본 그대로 분석을 한 뒤 첨삭을 진행해야해.\n");
        
        

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
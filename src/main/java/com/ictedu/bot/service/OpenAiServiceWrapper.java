package com.ictedu.bot.service;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.embedding.EmbeddingRequest;
import com.theokanning.openai.fine_tuning.FineTuningJob;
import com.theokanning.openai.fine_tuning.FineTuningJobRequest;
import com.theokanning.openai.model.Model;
import com.theokanning.openai.service.OpenAiService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OpenAiServiceWrapper {
    
	private static final Logger logger = LoggerFactory.getLogger(OpenAiServiceWrapper.class);
	private final OpenAiService openAiService;
	private final String modelId;
	private final String fineTunedModelId;

	public OpenAiServiceWrapper(
	    @Value("${spring.ai.openai.api-key}") String apiKey,
	    @Value("${spring.ai.openai.model.id}") String modelId,
	    @Value("${spring.ai.openai.finetuned.model.id}") String fineTunedModelId) {
	    this.openAiService = new OpenAiService(apiKey);
	    this.modelId = modelId;
	    this.fineTunedModelId = fineTunedModelId;
	}
    public String generateResponse(String userInput) {
        try {
            var messages = Arrays.asList(
                new ChatMessage("system", "안녕하세요! 저는 Focusjob 플랫폼의 챗봇 Force입니다. 취업, 면접, 직장 내 인간관계 및 직무 발전에 관한 질문에 대해 정확하고 유용한 정보를 제공해 드립니다. 친절하고 존중하는 말투로 응답하며, 한국어로 정확한 맞춤법과 문법을 사용하겠습니다. 비속어에는 반응하지 않으니, 예의 바른 대화를 부탁드립니다!\r\n"
                		+ ""),
                new ChatMessage("user", userInput)
            );

            var completionRequest = ChatCompletionRequest.builder()
                    .model(modelId)
                    .messages(messages)
                    .build();
            var response = openAiService.createChatCompletion(completionRequest);
            if (response.getChoices().isEmpty()) {
                throw new RuntimeException("OpenAI에서 응답이 생성되지 않았습니다.");
            }
            return response.getChoices().get(0).getMessage().getContent();
        } catch (Exception e) {
            logger.error("OpenAI에서 응답 생성 실패", e);
            throw new RuntimeException("OpenAI에서 응답 생성에 실패했습니다.", e);
        }
    }
    
    public String generateResponseWithFineTunedModel(String prompt) {
        try {
            var messages = Arrays.asList(
            		new ChatMessage("system", "안녕하세요. Focusjob 플랫폼의 친근하고 전문적인 챗봇 force 입니다. 취업 및 면접 관련 질문에 정확하고 유용한 정보를 제공하며, 사용자를 존중하는 말투로 답변합니다. 모든 응답은 반드시 한국어로 제공해야 하고, 욕설에는 대응하지 않습니다."),
                new ChatMessage("user", prompt)
            );

            var completionRequest = ChatCompletionRequest.builder()
                    .model(fineTunedModelId)
                    .messages(messages)
                    .build();
            var response = openAiService.createChatCompletion(completionRequest);
            if (response.getChoices().isEmpty()) {
                throw new RuntimeException("파인 튜닝된 모델에서 응답이 생성되지 않았습니다.");
            }
            return response.getChoices().get(0).getMessage().getContent();
        } catch (Exception e) {
            logger.error("파인 튜닝된 모델을 사용한 응답 생성 실패", e);
            throw new RuntimeException("파인 튜닝된 모델을 사용한 응답 생성에 실패했습니다.", e);
        }
    }
    public String startFineTuning(String trainingFilePath) {
        FineTuningJobRequest request = FineTuningJobRequest.builder()
            .trainingFile(trainingFilePath)
            .model("gpt-3.5-turbo")  // 또는 다른 적절한 모델
            .build();

        FineTuningJob job = openAiService.createFineTuningJob(request);
        return job.getId();
    }

    public FineTuningJob getFineTuneStatus(String jobId) {
        return openAiService.retrieveFineTuningJob(jobId);
    }
    
    public List<Double> createEmbedding(String text) {
        EmbeddingRequest embeddingRequest = EmbeddingRequest.builder()
                .model("text-embedding-ada-002")
                .input(Arrays.asList(text))
                .build();
        
        return openAiService.createEmbeddings(embeddingRequest).getData().get(0).getEmbedding();
    }

    //파인 튜닝된 모델 목록을 조회
    public List<FineTuningJob> listFineTuningJobs() {
        return openAiService.listFineTuningJobs();
    }

	public List<String> listFineTunedModels() {
		 return openAiService.listModels().stream()
	                .filter(model -> model.getId().startsWith("ft:"))
	                .map(Model::getId)
	                .collect(Collectors.toList());
	    }

	// 모델 삭제 대신 사용 중단 처리
	public void deprecateFineTunedModel(String modelId) {
        // OpenAI API에서는 실제로 모델을 삭제하거나 deprecate하는 기능이 없으므로,
        // 이 메소드는 로깅 목적으로만 사용합니다.
        logger.info("Model {} has been marked as deprecated in the local system.", modelId);
    }

    public void cancelFineTuningJob(String jobId) {
        openAiService.cancelFineTuningJob(jobId);
    }

    public String generateResponseWithContext(List<ChatMessage> messages) {
        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(modelId)
                .messages(messages)
                .build();
        ChatCompletionResult result = openAiService.createChatCompletion(request);
        return result.getChoices().get(0).getMessage().getContent();
    }
	public Object createChatCompletion(ChatCompletionRequest completionRequest) {
		// TODO Auto-generated method stub
		return null;
	}
}
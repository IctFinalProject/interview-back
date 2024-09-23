package com.ictedu.interview.service;

import java.time.LocalDateTime;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ictedu.interview.model.dto.VideoDetailsDTO;
import com.ictedu.interview.model.entity.ClaudeAnalysis;
import com.ictedu.interview.model.entity.Interview;
import com.ictedu.interview.model.entity.Question;
import com.ictedu.interview.model.entity.VideoAnalysis;
import com.ictedu.interview.model.entity.VideoEntity;
import com.ictedu.interview.model.entity.VideoSpeechAnalysis;
import com.ictedu.interview.repository.ClaudeAnalysisRepository;
import com.ictedu.interview.repository.InterviewRepository;
import com.ictedu.interview.repository.QuestionRepository;
import com.ictedu.interview.repository.VideoAnalysisRepository;
import com.ictedu.interview.repository.VideoRepository;
import com.ictedu.interview.repository.VideoSpeechAnalysisRepository;
import com.ictedu.user.model.entity.User;

import jakarta.transaction.Transactional;

@Service
public class InterviewService {
	
    @Autowired
    private RestTemplate restTemplate;
    
	private final QuestionRepository questionRepository;
	private final InterviewRepository interviewRepository;
	private final VideoRepository videoRepository;
	private final VideoAnalysisRepository videoAnalysisRepository;
	private final VideoSpeechAnalysisRepository videoSpeechAnalysisRepository;
	private final ClaudeAnalysisRepository claudeAnalysisRepository;
	
	public InterviewService(QuestionRepository questionRepository, InterviewRepository interviewRepository, VideoRepository videoRepository, VideoAnalysisRepository videoAnalysisRepository, VideoSpeechAnalysisRepository videoSpeechAnalysisRepository, ClaudeAnalysisRepository claudeAnalysisRepository) {
		this.questionRepository = questionRepository;
		this.interviewRepository = interviewRepository;
		this.videoRepository = videoRepository;
		this.videoAnalysisRepository = videoAnalysisRepository;
		this.videoSpeechAnalysisRepository = videoSpeechAnalysisRepository;
		this.claudeAnalysisRepository = claudeAnalysisRepository;
	}

	@Value("${interview.api.key}")
    private String interviewApiKey;
	

	public List<String> getKeywordsFromResume(String motivation, String selfIntroduction) {
	    // motivation과 selfIntroduction에서 키워드를 분리하여 리스트로 변환
	    List<String> motivationKeywords = Arrays.asList(motivation.split(", "));
	    List<String> selfIntroductionKeywords = Arrays.asList(selfIntroduction.split(", "));
	    
	    // 두 리스트를 합쳐서 하나의 리스트로 반환
	    List<String> combinedKeywords = new ArrayList<>();
	    combinedKeywords.addAll(motivationKeywords);
	    combinedKeywords.addAll(selfIntroductionKeywords);
	    
	    return combinedKeywords;
	}
	@Transactional
	public List<Question> generateQuestions(String type, List<String> keywords, int count, User user, String desiredCompany) {
	    List<Question> questions = new ArrayList<>();

	    Interview interview = Interview.builder()
	            .userId(user)
	            .interviewType(type)
	            .startTime(LocalDateTime.now())
	            .endTime(LocalDateTime.now().plusHours(1))
	            .overallFeedback("전체적인 피드백")
	            .createdTime(LocalDateTime.now())
	            .updatedTime(LocalDateTime.now())
	            .build();

	    Interview savedInterview = interviewRepository.save(interview);

	    List<String> companies = Arrays.asList(desiredCompany.split(", "));
	    
	    if ("resume".equals(type)) {
	    // 회사 관련 질문 생성 (3개)
		    for (int i = 0; i < 3; i++) {
		        String company = companies.get(i % companies.size());
		        String prompt = createCompanyPrompt(company, keywords);
		        String questionText = callChatGPTApi(prompt);
		        String script = generateScript(questionText, keywords, company);
		        List<String> generatedKeywords = extractKeywords(questionText + " " + script);
		        String keywordsString = String.join(", ", generatedKeywords);
	
		        Question question = Question.builder()
		                .questionText(questionText)
		                .questionType("resume")
		                .script(script)
		                .keywords(keywordsString)
		                .createdTime(LocalDateTime.now())
		                .interviewId(savedInterview)
		                .build();
	
		        questions.add(question);
		    }

	    } else if("common".equals(type)) {
		    	for (int i = 0; i < 3; i++) {
		    		String company = companies.get(i % companies.size());
			        String prompt = createCommonPrompt(keywords);
			        String questionText = callChatGPTApi(prompt);
			        String script = generateScript(questionText, keywords, company);
			        List<String> generatedKeywords = extractKeywords(questionText + " " + script);
			        String keywordsString = String.join(", ", generatedKeywords);
		
			        Question question = Question.builder()
			                .questionText(questionText)
			                .questionType("Common")
			                .script(script)
			                .keywords(keywordsString)
			                .createdTime(LocalDateTime.now())
			                .interviewId(savedInterview)
			                .build();
		
			        questions.add(question);
			    }
	    	}
		    questionRepository.saveAll(questions);
		    return questions;
		}

	private String createCommonPrompt(List<String> keywords) {
	    String[] questions = {
	        "팀워크와 협업의 중요성에 대해 어떻게 생각하시나요? 과거의 경험을 바탕으로 팀 내에서 어떻게 협력하며 문제를 해결해왔는지 말씀해주시겠어요?",
	        "입사 후 어떤 목표를 달성하고 싶으신가요? 이를 위해 어떤 구체적인 계획이나 전략을 가지고 계신가요?",
	        "이 직장을 선택한 이유와 기대하는 점은 무엇인가요? 특히 이 회사가 귀하의 경력 발전에 어떻게 기여할 것이라고 생각하시는지 말씀해 주시겠어요?",
	        "업무와 개인 생활의 균형을 어떻게 유지하고 계신가요? 이를 위해 어떤 방법이나 습관을 실천하고 계신지 설명해 주세요.",
	        "팀 내에서 리더십을 발휘한 경험에 대해 설명해 주시겠어요? 어떤 상황에서 리더십을 발휘했고, 그 결과는 어땠는지 구체적으로 말씀해 주세요.",
	        "이 회사에서 이루고 싶은 장기적인 비전은 무엇인가요? 그 비전을 달성하기 위해 어떤 노력을 기울일 계획이신지 알려주세요."
	    };
	    Random random = new Random();
	    String selectedQuestion = questions[random.nextInt(questions.length)];

	    String basePrompt = "당신은 경험 많은 면접관입니다. 다음의 키워드를 바탕으로 간결하고 명확한 개방형 질문을 1개 작성해주세요:\n" +
	            selectedQuestion + "\n\n" +
	            "키워드: %s\n\n" +
	            "주의사항:\n" +
	            "- 질문은 간결하면서도 지원자의 경험과 역량을 잘 평가할 수 있도록 작성하세요.\n" +
	            "- 개방형 질문으로 구성하여 상세한 답변을 유도하세요.\n" +
	            "- 예시 문구(질문 예시, 지원자님 등)는 사용하지 마세요.\n" +
	            "- 정중한 표현으로 마무리 해주세요.";
		    return String.format(basePrompt, String.join(", ", keywords));
	}

	private String createCompanyPrompt(String company, List<String> keywords) {
	    String basePrompt = "당신은 %s 회사의 면접관입니다. 다음 키워드를 바탕으로 우리 회사에 지원한 후보자에게 물어볼 수 있는 구체적이고 깊이 있는 질문을 1개 생성해주세요:\n\n" +
	            "키워드: %s\n\n" +
	            "주의사항:\n" +
	            "- 회사의 특성과 산업을 고려한 질문을 만들어주세요.\n" +
	            "- 지원자의 경험과 우리 회사와의 연관성을 파악할 수 있는 질문으로 구성해주세요.\n" +
	            "- 개방형 질문으로 구성하여 상세한 답변을 유도해주세요.\n" +
	            "- 예시 문구(질문 예시, 지원자님 등)는 사용하지 마세요."+
	            "- 정중한 표현으로 마무리 해주세요.";

	    return String.format(basePrompt, company, String.join(", ", keywords));
	}
	
	
	private String callChatGPTApi(String prompt) {
	    // ChatGPT API 설정
	    String apiUrl = "https://api.openai.com/v1/chat/completions";
	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);
	    headers.setBearerAuth(interviewApiKey);

	    Map<String, Object> requestBody = new HashMap<>();
	    requestBody.put("model", "gpt-3.5-turbo");
	    requestBody.put("messages", Arrays.asList(
	            Map.of("role", "system", "content", "당신은 한국어로 응답하는 면접 전문가입니다."),
	            Map.of("role", "user", "content", prompt)
	    ));

	    HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
	    ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, request, Map.class);

	    if (response.getStatusCode() == HttpStatus.OK) {
	        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
	        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
	        return (String) message.get("content");
	    } else {
	        String errorMessage = response.getBody() != null ? response.getBody().toString() : "Unknown error";
	        throw new RuntimeException("ChatGPT API 호출 실패: " + response.getStatusCode() + " - " + errorMessage);
	    }
	}
	
	private String generateScript(String questionText, List<String> keywords, String companyName) {
	    String scriptPrompt = "당신은 면접에 임하는 지원자입니다. 다음 질문에 대해 1분 이내로 답변할 수 있는 구체적이고 논리적인 답변을 작성해 주세요. " +
	        "이 답변은 면접 분석 기준에 따라 평가될 예정이므로 각 항목을 충족하도록 유의해 주세요.\n\n" +
	        "질문: " + questionText + "\n\n" +
	        "답변 작성 가이드라인:\n" +
	        "1. **시간 제한**: 답변은 1분(약 150-200단어) 이내로 간결하게 작성하세요.\n" +
	        "2. **내용 분석**: 답변은 논리적으로 구성하고, 핵심 아이디어를 명확하게 전달하세요. 가능한 경우 간단한 예시를 포함하되, 실제 경험이 없는 내용은 언급하지 마세요.\n" +
	        "3. **감정 분석**: 긍정적이고 자신감 있는 어조를 유지하세요.\n" +
	        "4. **언어 사용**: 전문적인 어휘와 업계 관련 용어를 적절히 사용하되, 간결성을 유지하세요.\n" +
	        "5. **어조 및 텐션**: 답변의 어조는 일관되고 자연스럽게 유지하세요. 불필요한 반복이나 주저를 피하세요.\n" +
	        "6. **회사 관련성**: " + companyName + "에 대한 관심과 이해를 보여주되, 해당 회사에서의 직접적인 경험이나 프로젝트를 언급하지 마세요. 대신 당신의 기술과 경험이 어떻게 회사의 요구사항과 일치하는지 설명하세요.\n" +
	        "7. **질문 이해도**: 질문의 핵심을 정확히 파악하고 그에 맞는 답변을 제공하세요.\n" +
	        "8. **진실성**: 실제 경험과 지식에 기반한 답변만 제공하세요. 가상의 경험이나 허위 정보를 포함하지 마세요.\n" +
	        "답변의 모든 항목을 만족하면서도 1분 이내로 전달할 수 있도록 작성해 주세요. 이 기준에 따라 답변이 평가될 것입니다.\n\n" +
	        "키워드를 반영한 답변을 작성해 주세요. 주요 키워드: " + String.join(", ", keywords) + "\n" +
	        "답변 형식: [답변 시작] 실제 답변 내용 [답변 종료]\n";

	    return callChatGPTApi(scriptPrompt);
	}
	private List<String> extractKeywords(String text) {
	    String keywordPrompt = String.format(
	        "당신은 키워드 추출 전문가입니다. 다음 텍스트에서 면접 질문과 답변에 관련된 주요 키워드를 5개 이내로 추출해주세요. " +
	        "키워드는 쉼표로 구분하여 나열해주세요:\n\n텍스트: %s\n\n주의사항:\n" +
	        "- 추출된 키워드는 질문의 핵심 주제나 답변에서 강조된 역량, 경험을 대표해야 합니다.\n" +
	        "- 일반적인 단어보다는 해당 직무나 산업과 관련된 전문적인 용어를 선호합니다.\n" +
	        "- 키워드는 단일 단어 또는 짧은 구문으로 제한해주세요.", text);

	    String keywordsString = callChatGPTApi(keywordPrompt);
	    return Arrays.asList(keywordsString.split(","));
	}
	
	public List<Question> generateRealQuestions(List<String> resumeKeywords, User user, String desiredCompany) {
	    List<Question> questions = new ArrayList<>();
	    
	    Interview interview = Interview.builder()
	            .userId(user)
	            .interviewType("real")
	            .startTime(LocalDateTime.now())
	            .endTime(LocalDateTime.now().plusHours(1))
	            .overallFeedback("실전 면접 피드백")
	            .createdTime(LocalDateTime.now())
	            .updatedTime(LocalDateTime.now())
	            .build();
	    
	    Interview savedInterview = interviewRepository.save(interview);
	    
	    List<String> questionTypes = Arrays.asList("공통 질문", "이력서 기반 질문", "성향 파악 질문", "상황 질문", "보상 선호 질문", "회사 관련 질문");
	    List<String> companies = Arrays.asList(desiredCompany.split(", "));
	    
	    // 질문 생성
	    for (String type : questionTypes) {
	        String prompt = createRealPrompt(type, resumeKeywords, companies);
	        String questionText = callChatGPTApi(prompt);
	        String script = generateScript(questionText, resumeKeywords, "");
	        List<String> generatedKeywords = extractKeywords(questionText + " " + script);
	        String keywordsString = String.join(", ", generatedKeywords);
	        
	        Question question = Question.builder()
	                .questionText(questionText)
	                .questionType(type)
	                .script(script)
	                .keywords(keywordsString)
	                .createdTime(LocalDateTime.now())
	                .interviewId(savedInterview)
	                .build();
	        
	        questions.add(question);
	    }
	    
	    questionRepository.saveAll(questions);
	    
	    return questions;
	}

	private String createRealPrompt(String type, List<String> resumeKeywords, List<String> companies) {
	    String basePrompt = "다음 정보를 바탕으로 실제 면접에서 물어볼 수 있는 %s 관련 질문을 생성해주세요.\n\n"
	        + "이력서 키워드: %s\n\n"
	        + "지원 회사: %s\n\n"
	        + "질문 유형:\n"
	        + "1번 공통 질문: 지원자의 경험과 역량을 전반적으로 파악할 수 있는 질문\n"
	        + "2번 이력서 기반 질문: 지원자의 이력서나 자기소개서에 언급된 내용을 깊이 있게 탐색하는 질문\n"
	        + "3번 성향 파악 질문: 지원자의 성격, 가치관, 업무 스타일을 파악하는 질문\n"
	        + "4번 상황 질문: 특정 상황에서의 대처 능력을 확인하는 질문\n"
	        + "5번 보상 선호 질문: 지원자의 동기 부여 요인을 파악하는 질문\n"
	        + "6번 회사 관련 질문: 지원 회사에 대한 이해도와 적합성을 확인하는 질문\n\n"
	        + "주의사항:\n"
	        + "- 구체적이고 깊이 있는 질문을 만들어주세요.\n"
	        + "- 지원자의 경험과 역량을 파악할 수 있는 개방형 질문으로 구성해주세요.\n"
	        + "- 실제 면접관이 물어볼 법한 난이도의 질문을 만들어주세요.\n"
	        + "- 필요한 경우, 꼬리 질문을 추가하여 더 깊이 있는 답변을 유도할 수 있게 해주세요.\n"
	        + "- 질문은 정중한 표현으로 끝나도록 해주세요.\n"
	        + "- 질문을 생성할때 1번,2번,3번,4번...9번을 붙이지 마세요.\n"
	        + "- 질문은 자연스러운 문장 구조로 만들어주세요.\n\n"
	        + "질문 예시:\n"
	        + "직장 또는 프로젝트에서 몰입하여 성과를 낸 경험에 대해 말씀해 주시겠어요? 어떤 점에 집중하셨고, 그 경험을 통해 어떤 교훈을 얻으셨나요?\n"
	        + "자기소개서에서 '솔선수범'이라는 표현을 사용하셨는데, 구체적인 사례와 그로 인한 팀이나 조직의 변화에 대해 설명해 주시겠어요?\n"
	        + "우리 회사의 최근 기술 동향이나 시장 전략에 대한 견해와 함께, 지원자님께서 어떤 방식으로 기여할 수 있을지 구체적으로 말씀해 주시겠어요?\n\n"
	        + "위의 예시와 같은 형식으로, %s 유형에 맞는 질문을 1개 생성해주세요. 질문은 자연스러운 대화체로 만들어주시고, 꼬리 질문이 있다면 같은 문단 안에 포함시켜 주세요.";

	    return String.format(basePrompt, type, String.join(", ", resumeKeywords), String.join(", ", companies), type);
	}

	@Transactional
	public List<VideoEntity> getResultsByIds(Long userIdLong) {
		List<VideoEntity> videos = videoRepository.findAllByUserId(userIdLong);
		return videos;
	}

    @Transactional
    public void deleteVideoById(Long videoId) {
    	VideoEntity video = videoRepository.findById(videoId)
    	        .orElseThrow(() -> new RuntimeException("Video not found"));

    	    // ClaudeAnalysis 삭제 (만약 존재한다면)
    	    claudeAnalysisRepository.deleteByVideo(video);

    	    // VideoSpeechAnalysis 삭제
    	    videoSpeechAnalysisRepository.deleteByVideo(video);

    	    // VideoAnalysis 삭제
    	    videoAnalysisRepository.deleteByVideo(video);

    	    // VideoEntity 삭제
    	    videoRepository.delete(video);
	
    }

    public VideoDetailsDTO getVideoById(Long videoIdLong) {
        List<VideoEntity> videos = videoRepository.findAllById(videoIdLong);
        List<VideoAnalysis> videoAnalyses = videoAnalysisRepository.findAllByVideoId(videoIdLong);
        List<VideoSpeechAnalysis> videoSpeechAnalyses = videoSpeechAnalysisRepository.findAllByVideoId(videoIdLong);
        List<ClaudeAnalysis> claudeAnalyses = claudeAnalysisRepository.findAllByVideoId(videoIdLong);

        VideoDetailsDTO videoDetailsDTO = new VideoDetailsDTO();
        videoDetailsDTO.setVideos(videos);
        videoDetailsDTO.setVideoAnalyses(videoAnalyses);
        videoDetailsDTO.setVideoSpeechAnalyses(videoSpeechAnalyses);
        videoDetailsDTO.setClaudeAnalyses(claudeAnalyses);

        return videoDetailsDTO;
    }
}

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

import com.ictedu.interview.model.entity.Interview;
import com.ictedu.interview.model.entity.Question;
import com.ictedu.interview.model.entity.VideoEntity;
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
	
	public InterviewService(QuestionRepository questionRepository, InterviewRepository interviewRepository, VideoRepository videoRepository, VideoAnalysisRepository videoAnalysisRepository, VideoSpeechAnalysisRepository videoSpeechAnalysisRepository) {
		this.questionRepository = questionRepository;
		this.interviewRepository = interviewRepository;
		this.videoRepository = videoRepository;
		this.videoAnalysisRepository = videoAnalysisRepository;
		this.videoSpeechAnalysisRepository = videoSpeechAnalysisRepository;
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
	public List<Question> generateQuestions(String type, List<String> keywords, int count, User user) {
	    // 질문 리스트 생성
	    List<Question> questions = new ArrayList<>();
	    
	    // Interview 빌더로 인터뷰 객체 생성 (아직 DB에 저장 안 함)
	    Interview interview = Interview.builder()
	            .userId(user)  // 면접을 보는 사용자 ID
	            .interviewType(type)  // 면접 유형 (MOCK or REAL)
	            .startTime(LocalDateTime.now())  // 면접 시작 시간
	            .endTime(LocalDateTime.now().plusHours(1))  // 면접 종료 시간
	            .overallFeedback("전체적인 피드백")  // 전체 피드백 (임시값)
	            .createdTime(LocalDateTime.now())  // 생성 시간
	            .updatedTime(LocalDateTime.now())  // 수정 시간
	            .build();
	    
	    // Interview를 먼저 DB에 저장하여 interview_id 생성
	    Interview savedInterview = interviewRepository.save(interview);

	    // 질문 생성 및 interview_id 설정
	    for (int i = 0; i < count; i++) {
	        String prompt = createPrompt(type, keywords);
	        String questionText = callChatGPTApi(prompt);
	        String script = generateScript(questionText);       
	        List<String> generatedKeywords = extractKeywords(questionText + " " + script);
	        // List<String>을 String으로 변환 (각 요소를 쉼표로 구분)
	        String keywordsString = String.join(", ", generatedKeywords);
	        // Question 빌더로 질문 객체 생성 및 interview_id 설정
	        Question question = Question.builder()
	                .questionText(questionText)
	                .questionType(type)
	                .script(script)
	                .keywords(keywordsString)
	                .createdTime(LocalDateTime.now())
	                .interviewId(savedInterview)  // 생성된 Interview의 ID를 설정
	                .build();
	        
	        questions.add(question);
	    }

	    // 질문을 DB에 저장
	    questionRepository.saveAll(questions);

	    return questions;
	}
	
	private String createPrompt(String type, List<String> keywords) {
		 String basePrompt = "경험 많은 면접관으로서, %s 면접에 적합한 간결하고 자연스러운 질문을 1개 생성해주세요.\n\n" +
			        "키워드: %s\n\n" +
			        "주의사항:\n" +
			        "- 실제 대화처럼 자연스럽고 간결한 질문을 만들어주세요.\n" +
			        "- 지원자의 경험과 역량을 파악할 수 있는 개방형 질문으로 구성해주세요.\n" +
			        "- 존댓말을 사용하고, 질문의 길이는 이전보다 2/3 정도로 줄여주세요.";

	    return String.format(basePrompt, type, String.join(", ", keywords));
	}
	
	private String callChatGPTApi(String prompt) {
		// ChatGPT API 설정
		String apiUrl = "https://api.openai.com/v1/chat/completions";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(interviewApiKey); // 실제 API 키로 교체 필요
		
		// API 요청 본문 구성
		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("model", "gpt-3.5-turbo");
		requestBody.put("messages", Arrays.asList(
		        Map.of("role", "system", "content", "당신은 한국어로 응답하는 면접 전문가입니다."),
		        Map.of("role", "user", "content", prompt)
		));
		
		HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
		
		// API 호출 및 응답 처리
		ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, request, Map.class);
		
		if (response.getStatusCode() == HttpStatus.OK) {
		    List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
		    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
		    return (String) message.get("content");
		} else {
		    throw new RuntimeException("ChatGPT API 호출 실패: " + response.getStatusCode());
		}
	}
	/*
    private String generateScript(String questionText) {
        String scriptPrompt = "다음 면접 질문에 대한 모범 답변 스크립트를 한국어로 작성해주세요: " + questionText;
        return callChatGPTApi(scriptPrompt);
    }
    */
	private String generateScript(String questionText) {
		 String scriptPrompt = "당신은 면접 지원자입니다. 다음 질문에 대해 자연스럽고 설득력 있는 답변을 해주세요:\n\n" +
			        "질문: %s\n\n" +
			        "답변 작성 가이드라인:\n" +
			        "1. 구체적인 경험이나 상황을 예로 들어 설명하세요.\n" +
			        "2. 답변은 약 50초 분량으로, 간결하면서도 충분한 정보를 담아주세요.\n" +
			        "3. 자연스러운 대화체로 답변하되, 전문성과 열정이 느껴지도록 해주세요.\n" +
			        //"4. 가능하다면 STAR(상황-과제-행동-결과) 방식을 활용해 구조화된 답변을 제시하세요.\n" +
			        "4. 답변의 끝에는 질문과 연관된 추가적인 정보나 강점을 간단히 언급해 주세요.";

        return callChatGPTApi(String.format(scriptPrompt, questionText));
    }
	private List<String> extractKeywords(String text) {
        String keywordPrompt = "당신은 키워드 추출 전문가입니다. 다음 텍스트에서 면접 질문과 답변에 관련된 주요 키워드를 5개 이내로 추출해주세요. " +
                "키워드는 쉼표로 구분하여 나열해주세요:\n\n" +
                "텍스트: %s\n\n" +
                "주의사항:\n" +
                "- 추출된 키워드는 질문의 핵심 주제나 답변에서 강조된 역량, 경험을 대표해야 합니다.\n" +
                "- 일반적인 단어보다는 해당 직무나 산업과 관련된 전문적인 용어를 선호합니다.\n" +
                "- 키워드는 단일 단어 또는 짧은 구문으로 제한해주세요.";

        String keywordsString = callChatGPTApi(String.format(keywordPrompt, text));
        return Arrays.asList(keywordsString.split(","));
    }

	@Transactional
	public List<Question> generateRealQuestions(List<String> resumeKeywords, User user) {
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

	    // 실전 면접에 적합한 질문 유형들
	    List<String> questionTypes = Arrays.asList("공통 질문", "이력서 기반 질문", "성향 파악 질문", "상황 질문", "보상 선호 질문", "심층 구조화 질문");

	    for (String type : questionTypes) {
	        String prompt = createRealPrompt(type, resumeKeywords);
	        String questionText = callChatGPTApi(prompt);
	        String script = generateScript(questionText);       
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

	private String createRealPrompt(String type, List<String> resumeKeywords) {
		 String basePrompt = "다음 정보를 바탕으로 실제 면접에서 물어볼 수 있는 %s 관련 질문을 생성해주세요.\n\n" +
		            "이력서 키워드: %s\n\n" +
		            "질문 유형:\n" +
		            "1. 공통 질문: 지원자의 경험과 역량을 전반적으로 파악할 수 있는 질문\n" +
		            "2. 이력서 기반 질문: 지원자의 이력서나 자기소개서에 언급된 내용을 깊이 있게 탐색하는 질문\n" +
		            "3. 성향 파악 질문: 지원자의 성격, 가치관, 업무 스타일을 파악하는 질문\n" +
		            "4. 상황 질문: 특정 상황에서의 대처 능력을 확인하는 질문\n" +
		            "5. 보상 선호 질문: 지원자의 동기 부여 요인을 파악하는 질문\n" +
		            "6. 심층 구조화 질문: 지원자의 경험을 구체적으로 파악하는 STAR 기법 기반의 질문\n\n" +
		            "주의사항:\n" +
		            "- 구체적이고 깊이 있는 질문을 만들어주세요.\n" +
		            "- 지원자의 경험과 역량을 파악할 수 있는 개방형 질문으로 구성해주세요.\n" +
		            "- 실제 면접관이 물어볼 법한 난이도의 질문을 만들어주세요.\n" +
		            "- 필요한 경우, 꼬리 질문을 추가하여 더 깊이 있는 답변을 유도할 수 있게 해주세요.\n" +
		            "- 질문은 '~해주세요', '~말씀해주시겠어요?'와 같은 정중한 표현으로 끝나도록 해주세요.\n\n" +
		            "질문 예시:\n" +
		            "1. [공통 질문] \"귀하는 직장 또는 프로젝트에서 몰입하여 성과를 낸 경험이 있나요? 그때 어떤 점에 집중하였으며, 그 경험을 통해 배운 것이 있다면 무엇인가요?\"\n" +
		            "2. [이력서 기반 질문] \"자소서에서 '솔선수범'이라는 표현을 사용하셨습니다. 구체적으로 어떤 상황에서 솔선수범하셨으며, 그로 인해 팀이나 조직에 어떤 긍정적인 영향을 미쳤는지 설명해 주세요.\"\n" +
		            "3. [상황 질문] \"프로젝트 진행 중 예상치 못한 어려움이 발생했을 때, 창의성과 실험 정신을 발휘하여 문제를 해결한 사례를 말씀해 주시겠어요?\"\n\n" +
		            "위의 예시와 같은 형식으로, %s 유형에 맞는 질문을 1개 생성해주세요.";

		    return String.format(basePrompt, type, String.join(", ", resumeKeywords), type);
		}

	@Transactional
	public List<VideoEntity> getResultsByIds(Long userIdLong) {
		List<VideoEntity> videos = videoRepository.findAllByUserId(userIdLong);
		return videos;
	}

    @Transactional
    public void deleteVideoById(Long videoId) {
        // VideoSpeechAnalysis 삭제
        videoSpeechAnalysisRepository.deleteByVideoId(videoId);

        // VideoAnalysis 삭제
        videoAnalysisRepository.deleteByVideoId(videoId);

        // VideoEntity 삭제
        videoRepository.deleteById(videoId);
    }
	
}

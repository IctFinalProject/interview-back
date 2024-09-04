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
import com.ictedu.interview.repository.InterviewRepository;
import com.ictedu.interview.repository.QuestionRepository;
import com.ictedu.user.model.entity.User;

import jakarta.transaction.Transactional;

@Service
public class InterviewService {
	
    @Autowired
    private RestTemplate restTemplate;
    
	private final QuestionRepository questionRepository;
	private final InterviewRepository interviewRepository;
	
	public InterviewService(QuestionRepository questionRepository, InterviewRepository interviewRepository) {
		this.questionRepository = questionRepository;
		this.interviewRepository = interviewRepository;
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
	        String prompt = createPrompt(type, count, keywords);
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
	
	private String createPrompt(String type, int count, List<String> keywords) {
	    String basePrompt = "다음 키워드를 기반으로 '한국어'로 %s 면접 질문을 %d개 생성해주세요: %s";
	    return String.format(basePrompt, type, count, String.join(", ", keywords));
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
	
    private String generateScript(String questionText) {
        String scriptPrompt = "다음 면접 질문에 대한 모범 답변 스크립트를 한국어로 작성해주세요: " + questionText;
        return callChatGPTApi(scriptPrompt);
    }
    
    private List<String> extractKeywords(String text) {
        String keywordPrompt = "다음 텍스트에서 주요 키워드를 5개 이내로 추출해주세요: " + text;
        String keywordsString = callChatGPTApi(keywordPrompt);
        return Arrays.asList(keywordsString.split(","));
    }
}

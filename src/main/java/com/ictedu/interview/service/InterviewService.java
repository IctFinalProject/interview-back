package com.ictedu.interview.service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ictedu.interview.model.dto.VideoDTO;
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
        // 주석: 프롬프트를 더 간결하고 다양한 측면을 고려하도록 수정
        String basePrompt = "# 면접 질문 생성 프롬프트\n\n" +
            "당신은 경험 많은 면접관입니다. 다음 정보를 바탕으로 간결하면서도 깊이 있는 면접 질문을 생성해주세요:\n\n" +
            "이력서 키워드: %s\n\n" +
            "## 질문 생성 가이드라인:\n\n" +
            "1. **간결성**: 질문은 2-3문장 이내로 간결하게 구성하세요.\n" +
            "2. **다양성**: 기술적 역량뿐만 아니라 소프트 스킬, 경험, 가치관 등 다양한 측면을 고려하세요.\n" +
            "3. **개방성**: 상세한 답변을 유도하는 개방형 질문을 사용하세요.\n" +
            "4. **실용성**: 실제 업무 상황이나 경험과 연관된 질문을 만드세요.\n\n" +
            "## 주의사항:\n\n" +
            "- 질문은 정중하고 전문적인 어조로 끝나야 합니다.\n" +
            "- 지나치게 기술적인 용어는 피하고, 일반적인 업무 상황에 초점을 맞추세요.\n" +
            "- '면접 질문:' 등의 불필요한 prefix는 사용하지 마세요.\n\n" +
            "위의 가이드라인을 따라 질문을 1개 생성해주세요.";

        return String.format(basePrompt, String.join(", ", keywords));
    }

	// 수정된 createCompanyPrompt 메서드
    private String createCompanyPrompt(String company, List<String> keywords) {
        // 주석: 회사 특성을 고려한 질문 생성을 위해 프롬프트 개선
        String basePrompt = "# 회사 특화 면접 질문 생성 프롬프트\n\n" +
            "당신은 %s 회사의 면접관입니다. 다음 정보를 바탕으로 우리 회사에 지원한 후보자에게 물어볼 수 있는 구체적이고 깊이 있는 질문을 생성해주세요:\n\n" +
            "이력서 키워드: %s\n" +
            "회사: %s\n\n" +
            "## 질문 생성 가이드라인:\n\n" +
            "1. **회사 관련성**: 회사의 특성과 산업을 고려한 질문을 만드세요.\n" +
            "2. **경험 연관**: 지원자의 경험과 우리 회사와의 연관성을 파악할 수 있는 질문으로 구성하세요.\n" +
            "3. **구체성**: 가능한 경우 특정 상황이나 시나리오를 제시하여 지원자의 문제 해결 능력을 평가할 수 있게 하세요.\n" +
            "4. **개방성**: 상세한 답변을 유도하는 개방형 질문을 사용하세요.\n\n" +
            "## 주의사항:\n\n" +
            "- 질문은 정중하고 전문적인 어조로 끝나야 합니다.\n" +
            "- 질문 번호를 붙이지 마세요.\n" +
            "- 예시 답변이나 \"지원자님\" 등의 문구를 포함하지 마세요.\n\n" +
            "위의 가이드라인을 따라 질문을 1개 생성해주세요.";

        return String.format(basePrompt, company, String.join(", ", keywords), company);
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
	
	 // 수정된 generateScript 메서드
	private String generateScript(String questionText, List<String> keywords, String companyName) {
        // 주석: 1분 이내 답변을 위한 더 간결한 구조로 프롬프트 수정
        String scriptPrompt = "# 면접 답변 스크립트 생성 프롬프트\n\n" +
            "당신은 면접에 임하는 지원자입니다. 다음 질문에 대해 1분 이내(약 100-150단어)로 답변할 수 있는 간결하고 효과적인 답변을 작성해 주세요:\n\n" +
            "질문: " + questionText + "\n\n" +
            "## 답변 작성 가이드라인:\n\n" +
            "1. **핵심 포인트**: 2-3개의 주요 포인트에 집중하세요.\n" +
            "2. **구체적 예시**: 가능한 경우 짧은 구체적 예시를 한 개만 포함하세요.\n" +
            "3. **간결성**: 불필요한 세부 사항은 생략하고 핵심만 전달하세요.\n" +
            "4. **관련성**: " + companyName + "의 가치나 목표와 연결 지어 답변하세요.\n" +
            "5. **긍정적 마무리**: 답변 끝에 간단한 포부나 열정을 표현하세요.\n\n" +
            "## 주의사항:\n\n" +
            "- 답변은 자연스러운 대화체로 작성하세요.\n" +
            "- 과도한 기술 용어 사용을 피하세요.\n" +
            "- 허위 정보나 과장된 경험을 포함하지 마세요.\n\n" +
            "키워드 참고: " + String.join(", ", keywords) + "\n" +
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
	        // 주석: 실전 면접용 스크립트 생성 메서드 호출
	        String script = generateRealInterviewScript(questionText, resumeKeywords, desiredCompany);
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

	// 수정된 createRealPrompt 메서드
    private String createRealPrompt(String type, List<String> resumeKeywords, List<String> companies) {
        // 주석: 실제 면접 상황을 더 잘 반영하도록 프롬프트 개선
    	 String basePrompt = "# 실전 면접 질문 생성 프롬프트\n\n" +
    		        "다음 정보를 바탕으로 실제 면접에서 물어볼 수 있는 %s 관련 질문을 생성해주세요.\n\n" +
    		        "이력서 키워드: %s\n" +
    		        "지원 회사: %s\n\n" +
    		        "## 질문 유형 설명:\n" +
    		        "- 공통 질문: 지원자의 경험과 역량을 전반적으로 파악할 수 있는 질문\n" +
    		        "- 이력서 기반 질문: 지원자의 이력서나 자기소개서에 언급된 내용을 깊이 있게 탐색하는 질문\n" +
    		        "- 성향 파악 질문: 지원자의 성격, 가치관, 업무 스타일을 파악하는 질문\n" +
    		        "- 상황 질문: 특정 상황에서의 대처 능력을 확인하는 질문\n" +
    		        "- 보상 선호 질문: 지원자의 동기 부여 요인을 파악하는 질문\n" +
    		        "- 회사 관련 질문: 지원 회사에 대한 이해도와 적합성을 확인하는 질문\n\n" +
    		        "## 질문 생성 가이드라인:\n\n" +
    		        "1. **구체성**: 지원자의 경험과 역량을 깊이 있게 탐색할 수 있는 구체적인 질문을 만드세요.\n" +
    		        "2. **상황 기반**: 가능한 경우 특정 상황이나 시나리오를 제시하여 지원자의 문제 해결 능력을 평가할 수 있게 하세요.\n" +
    		        "3. **개방성**: 단순한 '예/아니오'로 답할 수 없는, 상세한 답변을 유도하는 개방형 질문을 사용하세요.\n" +
    		        "4. **직접성**: 질문을 직접적으로 제시하고, '질문:' 등의 접두사를 사용하지 마세요.\n\n" +
    		        "## 주의사항:\n\n" +
    		        "- 질문은 정중하고 전문적인 어조로 끝나야 합니다.\n" +
    		        "- 질문 번호를 붙이지 마세요.\n" +
    		        "- 예시 답변이나 '지원자님' 등의 문구를 포함하지 마세요.\n" +
    		        "- 후속 질문을 포함하지 마세요. 하나의 주요 질문만 생성하세요.\n\n" +
    		        "위의 가이드라인을 따라 %s 유형에 맞는 질문을 1개만 생성해주세요.";

        return String.format(basePrompt, type, String.join(", ", resumeKeywords), String.join(", ", companies), type);
    }
    
    // 수정된 generateRealInterviewScript 메서드 (1분 제한 고려)
    private String generateRealInterviewScript(String questionText, List<String> keywords, String companyName) {
        // 주석: 1분 제한의 실전 면접을 위한 간결하면서도 효과적인 답변 스크립트 생성
    	String scriptPrompt = "# 1분 실전 면접 답변 스크립트 생성 프롬프트\n\n" +
    	        "당신은 실제 면접에 임하는 지원자입니다. 다음 질문에 대해 정확히 150-180단어로 답변할 수 있는 간결하면서도 임팩트 있는 답변을 작성해 주세요:\n\n" +
    	        "질문: " + questionText + "\n\n" +
    	        "## 답변 작성 가이드라인:\n\n" +
    	        "1. **핵심 메시지**: 가장 중요한 1-2개의 포인트에 집중하세요.\n" +
    	        "2. **간결한 STAR**: 상황(S)과 결과(R)를 간략히 언급하고, 행동(A)에 초점을 맞추세요.\n" +
    	        "3. **구체적 예시**: 한 가지 명확하고 관련성 높은 예시를 간단히 언급하세요.\n" +
    	        "4. **회사 연관성**: " + companyName + "의 핵심 가치나 목표와 연결 지어 답변하세요.\n" +
    	        "5. **기술적 역량**: 가장 관련성 높은 기술이나 경험을 간단히 언급하세요.\n" +
    	        "6. **적응력 강조**: 빠른 학습능력이나 문제해결 능력을 짧게 보여주세요.\n" +
    	        "7. **열정 표현**: 답변 끝에 간단히 열정이나 포부를 표현하세요.\n\n" +
    	        "## 주의사항:\n\n" +
    	        "- 답변은 자연스럽고 명확한 어조로 작성하세요.\n" +
    	        "- 불필요한 세부 사항은 과감히 생략하세요.\n" +
    	        "- 핵심 메시지를 전달하는 데 집중하세요.\n" +
    	        "- 시간 제약을 고려하여 가장 중요한 정보만 포함하세요.\n" +
    	        "- 반드시 150-180단어 범위 내에서 답변을 작성하세요.\n\n" +
    	        "키워드 참고: " + String.join(", ", keywords) + "\n" +
    	        "답변 형식: [답변 시작] 실제 답변 내용 [답변 종료]\n" +
    	        "단어 수 확인: 생성된 답변의 단어 수를 꼭 확인하고, 150-180단어 범위 내에 있도록 조정해주세요.";

        return callChatGPTApi(scriptPrompt);
    }
	public List<VideoDTO> getResultsByIds(Long userIdLong) {
	    List<VideoEntity> videos = videoRepository.findAllByUserId(userIdLong);
	    return videos.stream()
	                 .map(video -> new VideoDTO(video.getId(), video.getFileName(), video.getFilePath(), video.getUserId(), video.getQuestionId(), video.getQuestionText(), video.getFileSize(), video.getUploadDate(), video.getAnswerDuration()))
	                 .collect(Collectors.toList());
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

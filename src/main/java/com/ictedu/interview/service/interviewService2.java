//package com.ictedu.interview.service;
//
//import com.ictedu.interview.dto.*;
//import com.ictedu.interview.entity.*;
//import com.ictedu.interview.repository.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.time.LocalDateTime;
//import java.util.*;
//import java.util.stream.Collectors;
//
//public class InterviewService {
//
//    private static final Logger logger = LoggerFactory.getLogger(InterviewService.class);
//
//    @Autowired
//    private RestTemplate restTemplate;
//
//    @Autowired
//    private QuestionRepository questionRepository;
//
//    @Autowired
//    private InterviewRepository interviewRepository;
//
//     @Value("${interview.api.key}")
//    private String interviewApiKey;
//    /**
//     * 모의 면접을 시작하고 질문을 생성하는 메서드
//     * @param requestDTO 면접 요청 데이터
//     * @return 생성된 면접 데이터
//     */
//    public InterviewDTO startMockInterview(InterviewRequestDTO requestDTO) {
//        logger.info("Starting mock interview for user: {}", requestDTO.getUserId());
//        logger.info("Keywords received: {}", requestDTO.getKeywords());
//
//        // 사용자 이력서에서 키워드 가져오기
//        List<String> dbKeywords = getKeywordsFromResume(requestDTO.getUserId());
//        logger.info("Keywords retrieved from database: {}", dbKeywords);
//
//        // 프론트엔드 키워드와 데이터베이스 키워드 결합
//        List<String> combinedKeywords = new ArrayList<>(requestDTO.getKeywords());
//        combinedKeywords.addAll(dbKeywords);
//        combinedKeywords = combinedKeywords.stream().distinct().collect(Collectors.toList());
//        logger.info("Combined keywords: {}", combinedKeywords);
//
//        // 일반 질문 생성
//        List<Question> commonQuestions = generateQuestions("common", combinedKeywords, 5);
//        List<QuestionDTO> commonQuestionDTOs = convertToDTOs(commonQuestions);
//
//        // 이력서 기반 질문 생성
//        List<Question> resumeQuestions = generateQuestions("resume", combinedKeywords, 5);
//        List<QuestionDTO> resumeQuestionDTOs = convertToDTOs(resumeQuestions);
//
//        // 면접 데이터 생성
//        InterviewDTO interviewDTO = new InterviewDTO();
//        interviewDTO.setInterviewId(generateInterviewId());
//        interviewDTO.setUserId(requestDTO.getUserId());
//        interviewDTO.setInterviewType("mock");
//        interviewDTO.setCommonQuestions(commonQuestionDTOs);
//        interviewDTO.setResumeQuestions(resumeQuestionDTOs);
//
//        // 면접 데이터 저장
//        saveInterview(interviewDTO);
//
//        logger.info("Mock interview started successfully. Interview ID: {}", interviewDTO.getInterviewId());
//        return interviewDTO;
//    }
//
//    /**
//     * 주어진 유형과 키워드를 기반으로 질문을 생성하는 메서드
//     * @param type 질문 유형 (common 또는 resume)
//     * @param keywords 키워드 리스트
//     * @param count 생성할 질문 수
//     * @return 생성된 질문 리스트
//     */
//    private List<Question> generateQuestions(String type, List<String> keywords, int count) {
//        logger.info("Generating {} questions using keywords: {}", type, keywords);
//        List<Question> questions = new ArrayList<>();
//        for (int i = 0; i < count; i++) {
//            String prompt = createPrompt(type, keywords);
//            String questionText = callChatGPTApi(prompt);
//            String script = generateScript(questionText);
//            List<String> generatedKeywords = extractKeywords(questionText + " " + script);
//
//            questions.add(Question.builder()
//                    .questionText(questionText)
//                    .questionType(type)
//                    .script(script)
//                    .keywords(generatedKeywords)
//                    .createdTime(LocalDateTime.now())
//                    .build());
//        }
//        logger.info("Generated {} {} questions", questions.size(), type);
//        return questions;
//    }
//
//    /**
//     * ChatGPT API 호출을 위한 프롬프트를 생성하는 메서드
//     * @param type 질문 유형
//     * @param keywords 키워드 리스트
//     * @return 생성된 프롬프트
//     */
//    private String createPrompt(String type, List<String> keywords) {
//        String basePrompt = "다음 키워드를 기반으로 한국어로 %s 면접 질문을 생성해주세요: %s";
//        return String.format(basePrompt, type, String.join(", ", keywords));
//    }
//
//    /**
//     * 주어진 질문에 대한 모범 답변 스크립트를 생성하는 메서드
//     * @param questionText 질문 텍스트
//     * @return 생성된 스크립트
//     */
//    private String generateScript(String questionText) {
//        String scriptPrompt = "다음 면접 질문에 대한 모범 답변 스크립트를 한국어로 작성해주세요: " + questionText;
//        return callChatGPTApi(scriptPrompt);
//    }
//
//    /**
//     * 주어진 텍스트에서 주요 키워드를 추출하는 메서드
//     * @param text 키워드를 추출할 텍스트
//     * @return 추출된 키워드 리스트
//     */
//    private List<String> extractKeywords(String text) {
//        String keywordPrompt = "다음 텍스트에서 주요 키워드를 5개 이내로 추출해주세요: " + text;
//        String keywordsString = callChatGPTApi(keywordPrompt);
//        return Arrays.asList(keywordsString.split(","));
//    }
//
//    /**
//     * ChatGPT API를 호출하는 메서드
//     * @param prompt API에 전달할 프롬프트
//     * @return API 응답 텍스트
//     */
//    private String callChatGPTApi(String prompt) {
//        // ChatGPT API 설정
//        String apiUrl = "https://api.openai.com/v1/chat/completions";
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.setBearerAuth("interviewApiKey"); // 실제 API 키로 교체 필요
//
//        // API 요청 본문 구성
//        Map<String, Object> requestBody = new HashMap<>();
//        requestBody.put("model", "gpt-3.5-turbo");
//        requestBody.put("messages", Arrays.asList(
//                Map.of("role", "system", "content", "당신은 한국어로 응답하는 면접 전문가입니다."),
//                Map.of("role", "user", "content", prompt)
//        ));
//
//        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
//
//        // API 호출 및 응답 처리
//        ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, request, Map.class);
//        
//        if (response.getStatusCode() == HttpStatus.OK) {
//            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
//            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
//            return (String) message.get("content");
//        } else {
//            throw new RuntimeException("ChatGPT API 호출 실패: " + response.getStatusCode());
//        }
//    }
//
//    /**
//     * 질문 엔티티를 DTO로 변환하는 메서드
//     * @param questions 질문 엔티티 리스트
//     * @return 변환된 질문 DTO 리스트
//     */
//    private List<QuestionDTO> convertToDTOs(List<Question> questions) {
//        return questions.stream()
//                .map(question -> QuestionDTO.builder()
//                        .id(question.getId())
//                        .content(question.getQuestionText())
//                        .script(question.getScript())
//                        .keywords(question.getKeywords())
//                        .questionTypeId(getQuestionTypeId(question.getQuestionType()))
//                        .orderNumber(1) // 기본 순서 설정
//                        .createdAt(question.getCreatedTime().toString())
//                        .build())
//                .collect(Collectors.toList());
//    }
//
//    /**
//     * 질문 유형에 따라 ID를 반환하는 메서드
//     * @param questionType 질문 유형
//     * @return 질문 유형 ID
//     */
//    private Long getQuestionTypeId(String questionType) {
//        switch (questionType) {
//            case "common":
//                return 1L;
//            case "resume":
//                return 2L;
//            default:
//                return 0L;
//        }
//    }
//
//    /**
//     * 사용자 이력서에서 키워드를 가져오는 메서드
//     * @param userId 사용자 ID
//     * @return 이력서에서 추출한 키워드 리스트
//     */
//    private List<String> getKeywordsFromResume(Long userId) {
//        // 이 부분은 실제 이력서 데이터베이스와 연동하여 구현해야 함
//        // 여기서는 예시로 더미 데이터를 반환
//        return Arrays.asList("Java", "Spring", "AI", "Machine Learning", "REST API");
//    }
//
//    /**
//     * 면접 ID를 생성하는 메서드
//     * @return 생성된 면접 ID
//     */
//    private Long generateInterviewId() {
//        // 실제 구현에서는 데이터베이스의 시퀀스나 다른 로직을 사용할 수 있음
//        return System.currentTimeMillis();
//    }
//
//    /**
//     * 면접 데이터를 저장하는 메서드
//     * @param interviewDTO 저장할 면접 데이터
//     */
//    private void saveInterview(InterviewDTO interviewDTO) {
//        Interview interview = Interview.builder()
//                .id(interviewDTO.getInterviewId())
//                .userId(interviewDTO.getUserId())
//                .interviewType(interviewDTO.getInterviewType())
//                .startTime(LocalDateTime.now())
//                .createdTime(LocalDateTime.now())
//                .build();
//
//        interviewRepository.save(interview);
//
//        // 질문 저장
//        List<Question> questions = new ArrayList<>();
//        questions.addAll(convertToEntities(interviewDTO.getCommonQuestions(), interview, "common"));
//        questions.addAll(convertToEntities(interviewDTO.getResumeQuestions(), interview, "resume"));
//        questionRepository.saveAll(questions);
//    }
//
//    /**
//     * 질문 DTO를 엔티티로 변환하는 메서드
//     * @param questionDTOs 변환할 질문 DTO 리스트
//     * @param interview 연관된 면접 엔티티
//     * @param type 질문 유형
//     * @return 변환된 질문 엔티티 리스트
//     */
//    private List<Question> convertToEntities(List<QuestionDTO> questionDTOs, Interview interview, String type) {
//        return questionDTOs.stream()
//                .map(dto -> Question.builder()
//                        .interview(interview)
//                        .questionText(dto.getContent())
//                        .questionType(type)
//                        .script(dto.getScript())
//                        .keywords(dto.getKeywords())
//                        .createdTime(LocalDateTime.now())
//                        .build())
//                .collect(Collectors.toList());
//    }
//
//	public List<String> getKeywordsFromResume(String motivation, String selfIntroduction) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//}
package com.ictedu.interview.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ictedu.interview.model.dto.QuestionDTO;
import com.ictedu.interview.model.dto.VideoDetailsDTO;
import com.ictedu.interview.model.entity.Question;
import com.ictedu.interview.model.entity.VideoEntity;
import com.ictedu.interview.service.InterviewService;
import com.ictedu.interview.service.QuestionService;
import com.ictedu.resume.entity.ResumeEntity;
import com.ictedu.resume.service.ResumeService;
import com.ictedu.user.model.entity.User;
import com.ictedu.user.repository.UserRepository;
import com.ictedu.user.service.UserService;

import lombok.extern.slf4j.Slf4j;

@CrossOrigin(origins = "http://localhost:3000") // CORS 설정 추가
@RestController
@RequestMapping("/api/interviews")
@Slf4j
public class InterviewController {
	
    private final UserService userService;
    private final ResumeService resumeService;
    private final InterviewService interviewService;
    private final UserRepository userRepository;
    private final QuestionService questionService;
    
    @Autowired
    public InterviewController(UserService userService, ResumeService resumeService, InterviewService interviewService, UserRepository userRepository, QuestionService questionService) {
        this.userService = userService;
        this.resumeService = resumeService;
        this.interviewService = interviewService;
        this.userRepository = userRepository;
        this.questionService = questionService;
    }
    /*
	@GetMapping("/getmockquestion")
	public ResponseEntity<?> getMockQuestions(@RequestParam("choosedResume") String choosedResume, @RequestParam("userId") String userId){
	    try {
	        Long userIdLong = Long.parseLong(userId); // String -> Long 변환
	        Long choosedResumeLong = Long.parseLong(choosedResume); // String -> Long 변환
	        // 변환 후 처리 로직
	        Optional<User> getUser = userRepository.findById(userIdLong);
	        User user = getUser.get(); // 값이 반드시 존재할 때 사용 (값이 없으면 예외 발생)
	        Optional<ResumeEntity> getResume = resumeService.findResumeById(choosedResumeLong);
			// List<ResumeEntity> 순회하면서 각 ResumeEntity의 User의 password를 출력
			String selfIntroduction = getResume.get().getKeywordsSelfIntroduction();
			String motivation = getResume.get().getKeywordsMotivation();
			List<String> dbKeywords = interviewService.getKeywordsFromResume(motivation, selfIntroduction);
			List<Question> commonQuestions = interviewService.generateQuestions("common", dbKeywords, 3, user);
			List<Question> resumeQuestions = interviewService.generateQuestions("resume", dbKeywords, 3, user);

	        // Map을 사용하여 두 리스트를 하나로 묶어 반환
	        Map<String, List<Question>> response = new HashMap<>();
	        response.put("commonQuestions", commonQuestions);
	        response.put("resumeQuestions", resumeQuestions);
	        System.out.println("끝났어요");
	        return ResponseEntity.ok(response);
	    } catch (NumberFormatException e) {
	        // 변환 실패 시 예외 처리
	        System.out.println("userId 변환 실패: " + e.getMessage());
	        return ResponseEntity.badRequest().body("유저 형식이 일치하지 않습니다!");
	    }
	}
	
	@GetMapping("/getrealquestion")
	public ResponseEntity<?> getRealQuestions(@RequestParam("choosedResume") String choosedResume, @RequestParam("userId") String userId){
	    try {
	        Long userIdLong = Long.parseLong(userId); // String -> Long 변환
	        Long choosedResumeLong = Long.parseLong(choosedResume); // String -> Long 변환
	        // 변환 후 처리 로직
	        Optional<User> getUser = userRepository.findById(userIdLong);
	        User user = getUser.get(); // 값이 반드시 존재할 때 사용 (값이 없으면 예외 발생)
	        Optional<ResumeEntity> getResume = resumeService.findResumeById(choosedResumeLong);
			// List<ResumeEntity> 순회하면서 각 ResumeEntity의 User의 password를 출력
			String selfIntroduction = getResume.get().getKeywordsSelfIntroduction();
			String motivation = getResume.get().getKeywordsMotivation();
			List<String> dbKeywords = interviewService.getKeywordsFromResume(motivation, selfIntroduction);
			List<Question> resumeQuestions = interviewService.generateRealQuestions(dbKeywords, user);

	        // Map을 사용하여 두 리스트를 하나로 묶어 반환
	        Map<String, List<Question>> response = new HashMap<>();
	        response.put("resumeQuestions", resumeQuestions);
	        System.out.println("실전 끝났어요");
	        return ResponseEntity.ok(response);
	    } catch (NumberFormatException e) {
	        // 변환 실패 시 예외 처리
	        System.out.println("userId 변환 실패: " + e.getMessage());
	        return ResponseEntity.badRequest().body("유저 형식이 일치하지 않습니다!");
	    }
	}*/
    @GetMapping("/getmockquestion")
    public ResponseEntity<?> getMockQuestions(@RequestParam("choosedResume") String choosedResume, @RequestParam("userId") String userId){
        try {
            Long userIdLong = Long.parseLong(userId);
            Long choosedResumeLong = Long.parseLong(choosedResume);
            Optional<User> getUser = userRepository.findById(userIdLong);
            User user = getUser.orElseThrow(() -> new RuntimeException("User not found"));
            Optional<ResumeEntity> getResume = resumeService.findResumeById(choosedResumeLong);
            ResumeEntity resume = getResume.orElseThrow(() -> new RuntimeException("Resume not found"));

            String selfIntroduction = resume.getKeywordsSelfIntroduction();
            String motivation = resume.getKeywordsMotivation();
            String desiredCompany = resume.getDesiredCompany();
            List<String> dbKeywords = interviewService.getKeywordsFromResume(motivation, selfIntroduction);
            
            List<Question> commonQuestions = interviewService.generateQuestions("common", dbKeywords, 3, user, desiredCompany);
            List<Question> resumeQuestions = interviewService.generateQuestions("resume", dbKeywords, 3, user, desiredCompany);

            Map<String, List<Question>> response = new HashMap<>();
            response.put("commonQuestions", commonQuestions);
            response.put("resumeQuestions", resumeQuestions);
            return ResponseEntity.ok(response);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("유저 또는 이력서 ID 형식이 일치하지 않습니다!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/getrealquestion")
    public ResponseEntity<?> getRealQuestions(@RequestParam("choosedResume") String choosedResume, @RequestParam("userId") String userId){
        try {
            Long userIdLong = Long.parseLong(userId);
            Long choosedResumeLong = Long.parseLong(choosedResume);
            Optional<User> getUser = userRepository.findById(userIdLong);
            User user = getUser.orElseThrow(() -> new RuntimeException("User not found"));
            Optional<ResumeEntity> getResume = resumeService.findResumeById(choosedResumeLong);
            ResumeEntity resume = getResume.orElseThrow(() -> new RuntimeException("Resume not found"));

            String selfIntroduction = resume.getKeywordsSelfIntroduction();
            String motivation = resume.getKeywordsMotivation();
            String desiredCompany = resume.getDesiredCompany();
            List<String> dbKeywords = interviewService.getKeywordsFromResume(motivation, selfIntroduction);
            
            List<Question> resumeQuestions = interviewService.generateRealQuestions(dbKeywords, user, desiredCompany);

            Map<String, List<Question>> response = new HashMap<>();
            response.put("resumeQuestions", resumeQuestions);
            return ResponseEntity.ok(response);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("유저 또는 이력서 ID 형식이 일치하지 않습니다!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping("/getinterviewquestions")
    public ResponseEntity<?> getInterviewQuestions(@RequestParam List<Long> questionId) {
        System.out.println("Received question IDs: " + questionId);
        List<Question> questions = questionService.getQuestionsByIds(questionId);
        
        // Question 엔티티를 QuestionDTO로 변환
        List<QuestionDTO> questionDTOs = questions.stream()
                .map(question -> new QuestionDTO(
                    question.getId(),
                    question.getQuestionText(),
                    question.getQuestionType(),
                    question.getScript(),
                    question.getKeywords(),
                    question.getCreatedTime(),
                    question.getUpdatedTime()
                ))
                .collect(Collectors.toList());
        System.out.println("퀘스천DTO: "+questionDTOs);
        return ResponseEntity.ok(questionDTOs);  // DTO를 반환
    }
    
    @GetMapping("/getinterviewresults")
    public ResponseEntity<?> getInterviewResults(@RequestParam("userId") String userId){
    	System.out.println("받은 userId: "+userId);
    	try {
    		Long userIdLong = Long.parseLong(userId); // String -> Long 변환
    		List<VideoEntity> response = interviewService.getResultsByIds(userIdLong);
    		return ResponseEntity.ok(response);
    	} catch (NumberFormatException e) {
	        // 변환 실패 시 예외 처리
	        System.out.println("userId 변환 실패: " + e.getMessage());
	        return ResponseEntity.badRequest().body("유저 형식이 일치하지 않습니다!");
    	}
    }
    
    
    @GetMapping("/fetchinterviewresult")
    public ResponseEntity<?> fetchInterviewResults(@RequestParam("videoId") String videoId) {
        System.out.println("받은 videoId: " + videoId);
        try {
            Long videoIdLong = Long.parseLong(videoId); // String -> Long 변환
            VideoDetailsDTO response = interviewService.getVideoById(videoIdLong);
            
            if (response == null) {
                System.out.println("해당 비디오가 존재하지 않습니다.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 비디오가 존재하지 않습니다.");
            }
            
            System.out.println("비디오 반환: " + response);
            return ResponseEntity.ok(response);  // DTO를 리턴
        } catch (NumberFormatException e) {
            // videoId가 Long으로 변환되지 못했을 때
            System.out.println("videoId 변환 실패: " + e.getMessage());
            return ResponseEntity.badRequest().body("videoId 형식이 일치하지 않습니다!");
        } catch (Exception e) {
            // 그 외 발생하는 모든 예외 처리
            System.out.println("서버 내부 오류: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버에서 처리 중 오류가 발생했습니다.");
        }
    }
}

package com.ictedu.interview.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ictedu.interview.model.entity.Question;
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
}

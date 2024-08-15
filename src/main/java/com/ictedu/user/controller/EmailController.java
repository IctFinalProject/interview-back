package com.ictedu.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ictedu.user.service.EmailService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/email")
public class EmailController {

    @Autowired
    private EmailService emailService;

    private Map<String, String> verificationCodes = new HashMap<>();

    @PostMapping("/send-code")
    public ResponseEntity<String> sendVerificationCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        if (email == null || email.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이메일을 입력하세요");
        }
        String code = emailService.sendVerificationCode(email);
        verificationCodes.put(email, code);
        return ResponseEntity.ok("인증코드를 전송하였습니다.");
    }

    @PostMapping("/verify-code")
    public ResponseEntity<String> verifyCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String code = request.get("code");
        String storedCode = verificationCodes.get(email);

        if (storedCode != null && storedCode.equals(code)) {
            verificationCodes.remove(email);
            return ResponseEntity.ok("인증이 완료되었습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("인증코드가 올바르지 않습니다!");
        }
    }
}

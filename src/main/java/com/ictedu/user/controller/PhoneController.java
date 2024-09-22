package com.ictedu.user.controller;

import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api/phone")
public class PhoneController {

    private final DefaultMessageService messageService;
    private final Map<String, String> phoneVerificationCodes = new HashMap<>();

    @Value("${sms.sender-number}") // 발신 번호를 application.yml에서 가져옵니다
    private String senderNumber;

    public PhoneController(
            @Value("${sms.api-key}") String apiKey,
            @Value("${sms.api-secret-key}") String apiSecretKey,
            @Value("${sms.base-url}") String baseUrl) {
        // 반드시 계정 내 등록된 유효한 API 키, API Secret Key를 입력해주셔야 합니다!
        this.messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecretKey, baseUrl);
    }

    @PostMapping("/send-code")
    public String sendVerificationCode(@RequestBody Map<String, String> request) {
        String phoneNumber = request.get("phone");
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return "핸드폰 번호를 입력해주세요";
        }

        String code = generateVerificationCode();
        phoneVerificationCodes.put(phoneNumber, code);

        Message message = new Message();
        message.setFrom(senderNumber); // 등록된 발신 번호 사용
        message.setTo(phoneNumber);
        message.setSubject("[FocusJob] 사용자님의 인증코드 입니다.");
        message.setText("인증코드는: " + code);

        SingleMessageSentResponse response = this.messageService.sendOne(new SingleMessageSendingRequest(message));
        System.out.println(response);

        return "인증번호가 전송되었습니다.";
    }

    @PostMapping("/verify-code")
    public ResponseEntity<String> verifyCode(@RequestBody Map<String, String> request) {
        String phoneNumber = request.get("phone");
        String code = request.get("code");
        String storedCode = phoneVerificationCodes.get(phoneNumber);

        if (storedCode != null && storedCode.equals(code)) {
            phoneVerificationCodes.remove(phoneNumber);
            return ResponseEntity.ok("핸드폰번호가 인증되었습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("인증코드가 유효하지 않습니다.");
        }
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }
}

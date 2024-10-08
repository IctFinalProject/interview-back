package com.ictedu.user.controller;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ictedu.payment.model.entity.PaymentInfo;
import com.ictedu.payment.repository.PaymentRepository;
import com.ictedu.payment.service.InputPayment;
import com.ictedu.payment.service.PaymentService;
import com.ictedu.user.model.entity.User;
import com.ictedu.user.repository.UserRepository;
import com.ictedu.user.service.InputUser;
import com.ictedu.user.service.UserService;

import lombok.extern.slf4j.Slf4j;

@CrossOrigin(origins = "http://localhost:3000") // CORS 설정 추가
@RestController
@RequestMapping("/api/auth")
@Slf4j
public class UserController {

	private final UserService userService;
	private final PaymentService paymentService;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	private final PaymentRepository paymentRepository;
	private final UserRepository userRepository;
	//토스 결제 키 값
	@Value("${toss.client.secret}")
	private String tossSecret;

	@Autowired
	public UserController(BCryptPasswordEncoder bCryptPasswordEncoder, UserService userService, PaymentService paymentService, PaymentRepository paymentRepository,UserRepository userRepository) {
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
		this.userService = userService;
		this.paymentService = paymentService;
		this.paymentRepository = paymentRepository;
		this.userRepository = userRepository;
	}

	@PostMapping("/user")
	public ResponseEntity<?> signUpUser(
			@RequestParam("email") String email,
			@RequestParam("password") String password,
			@RequestParam("username") String username,
			@RequestParam("phone") String phone,
			@RequestParam("address") String address,
			@RequestParam("gender") String gender,
			@RequestParam("birth") String birth,
			@RequestParam(value = "profileImage", required = false) MultipartFile profileImage) {
		try {
			InputUser inputUser = new InputUser(
					email, 
					password, 
					username, 
					phone, 
					address, 
					gender, 
					birth, 
					profileImage != null ? profileImage.getBytes() : null);
			User newUser = userService.signUpUser(inputUser);
			return ResponseEntity.ok(newUser);
		} catch (IOException e) {
			return ResponseEntity.status(514).body("프로필 사진에서 오류가 발생하였습니다.");
		}
	}

	@PostMapping("/edituser")
	public ResponseEntity<?> editUser(
	        @RequestParam("email") String email,
	        @RequestParam("username") String username,
	        @RequestParam("address") String address,
	        @RequestParam("birth") String birth,
	        @RequestParam(value = "profileImage", required = false) MultipartFile profileImage) {
	    try {
	        // 기존 유저 정보를 조회
	        Optional<User> existingUser = userService.findUserByEmail(email);
	        byte[] profileImageData;

	        // 사용자가 프로필 이미지를 수정한 경우 처리
	        if (profileImage != null && !profileImage.isEmpty()) {
	            profileImageData = profileImage.getBytes();
	        } else {
	            // 수정하지 않았을 경우 기존 프로필 이미지 유지
	            profileImageData = existingUser.get().getProfileImage();
	        }

	        InputUser inputUser = new InputUser(
	                email, 
	                username, 
	                address, 
	                birth, 
	                profileImageData);
	        boolean isUpdated = userService.updateUser(inputUser);

	        if (isUpdated) {
	            return ResponseEntity.ok("유저정보가 성공적으로 변경되었습니다.");
	        } else {
	            return ResponseEntity.status(515).body("유저정보 변경에 실패하였습니다.");
	        }
	    } catch (IOException e) {
	        return ResponseEntity.status(514).body("프로필 사진에서 오류가 발생하였습니다.");
	    }
	}

	@GetMapping("/findAllUser")
	public List<Map<String, Object>> getAllUsers() {
		List<User> users = userService.getAllUsers();

		// 결과를 저장할 리스트
		List<Map<String, Object>> result = new ArrayList<>();

		for (User user : users) {
			Map<String, Object> userMap = new HashMap<>();
			userMap.put("id", user.getId());
			userMap.put("email", user.getEmail());
			userMap.put("username", user.getUsername());
			userMap.put("address", user.getAddress());
			userMap.put("birth", user.getBirth());
			userMap.put("createdTime", user.getCreatedTime());
			userMap.put("deletedTime", user.getDeletedTime());
			userMap.put("gender", user.getGender());
			userMap.put("isActivated", user.getIsActivated());
			userMap.put("isAdmin", user.getIsAdmin());
			userMap.put("isDeleted", user.getIsDeleted());
			userMap.put("isEmailVerified", user.getIsEmailVerified());
			userMap.put("isGoogle", user.getIsGoogle());
			userMap.put("isKakao", user.getIsKakao());
			userMap.put("isNaver", user.getIsNaver());
			userMap.put("lastLogin", user.getLastLogin());
			userMap.put("password", user.getPassword());
			userMap.put("phone", user.getPhone());
			userMap.put("snsAccessToken", user.getSnsAccessToken());
			userMap.put("updatedTime", user.getUpdatedTime());

			// 프로필 이미지를 "data:image/jpeg;base64," 형식으로 변환
			if (user.getProfileImage() != null) {
				String base64Image = Base64.getEncoder().encodeToString(user.getProfileImage()); // 이미 Base64로 인코딩된 값이라고 가정
				userMap.put("profileImage", "data:image/jpeg;base64," + base64Image);
			} else {
				userMap.put("profileImage", null);
			}

			result.add(userMap);
		}
		return result;
	}

	@GetMapping("/findUserByEmail")
	public ResponseEntity<?> findUserByEmail(@RequestParam("email") String email){
		Optional<User> user = userService.findByEmail(email);
		return ResponseEntity.ok(user.get());
	}

	@GetMapping("/findPaymentInfoById")
	public ResponseEntity<?> findPaymentInfoById(@RequestParam("id") String id) {
		Optional<User> optionalUser = userService.findById(id);
		if (optionalUser.isEmpty()) {
			return ResponseEntity.notFound().build();
		}

		// 여러 개의 결제 정보를 가져오기 위해 findAllByUserId 사용
		List<PaymentInfo> paymentInfoList = paymentService.findAllByUserId(optionalUser.get());

		// isCanceled가 0이고, useCount가 1 이상인 결제 정보만 필터링
		List<PaymentInfo> filteredPaymentInfoList = paymentInfoList.stream()
		        .filter(paymentInfo -> paymentInfo.getIsCanceled() == 0 && paymentInfo.getUseCount() >= 1)
		        .collect(Collectors.toList());

		if (filteredPaymentInfoList.isEmpty()) {
		    return ResponseEntity.notFound().build();
		}

		return ResponseEntity.ok(filteredPaymentInfoList);
	}

	@GetMapping("/findAllPaymentInfo")
	public ResponseEntity<?> findAllPaymentInfo() {
		// 모든 결제 정보를 가져오기 위해 findAll 사용
		List<PaymentInfo> paymentInfoList = paymentService.findAll();

		// isCanceled가 0인 결제 정보만 필터링
		List<PaymentInfo> filteredPaymentInfoList = paymentInfoList.stream()
				.filter(paymentInfo -> paymentInfo.getIsCanceled() == 0)
				.collect(Collectors.toList());

		if (filteredPaymentInfoList.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(filteredPaymentInfoList);
	}

	@GetMapping("/findAllRefundInfo")
	public ResponseEntity<?> findAllRefundInfo() {
		// 모든 결제 정보를 가져오기 위해 findAll 사용
		List<PaymentInfo> paymentInfoList = paymentService.findAll();

		// isCanceled가 1인 결제 정보만 필터링
		List<PaymentInfo> filteredPaymentInfoList = paymentInfoList.stream()
				.filter(paymentInfo -> paymentInfo.getIsCanceled() == 1)
				.collect(Collectors.toList());

		if (filteredPaymentInfoList.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(filteredPaymentInfoList);
	}

	@GetMapping("/find-email")
	public ResponseEntity<?> findEmailByUsernameAndPhone(
			@RequestParam("username") String username, 
			@RequestParam("phone") String phone) {
		Optional<User> user = userService.findEmailByUsernameAndPhone(username, phone);
		if (user.isPresent()) {
			return ResponseEntity.ok(user.get().getEmail());
		} else {
			return ResponseEntity.status(404).body("사용자를 찾을 수 없습니다.");
		}
	}


	@GetMapping("/verify-user")
	public ResponseEntity<?> verifyUserByUsernameAndEmail(
			@RequestParam("username") String username, 
			@RequestParam("email") String email) {
		Optional<User> user = userService.verifyUserByUsernameAndEmail(username, email);
		if (user.isPresent()) {
			return ResponseEntity.ok(user.get().getEmail());
		} else {
			return ResponseEntity.status(404).body("사용자를 찾을 수 없습니다.");
		}
	}

	@PostMapping("/reset-password")
	public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
		String username = request.get("username");
		String email = request.get("email");
		String newPassword = request.get("newPassword");

		Optional<User> user = userService.verifyUserByUsernameAndEmail(username, email);
		String beforePassword = user.get().getPassword();
		if(bCryptPasswordEncoder.matches(newPassword, beforePassword)) {
			return ResponseEntity.status(512).body("이전 비밀번호는 사용할 수 없습니다.");
		}

		boolean isUpdated = userService.updatePassword(username, email, newPassword);

		if (isUpdated) {
			return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
		} else {
			return ResponseEntity.status(513).body("비밀번호 변경에 실패하였습니다.");
		}
	}

	@PostMapping("/changePassword")
	public ResponseEntity<?> changePassword(@RequestBody Map<String, String> request) {
		String username = request.get("username");
		String email = request.get("email");
		String newPassword = request.get("newPassword");
		String beforePasswordInput = request.get("currentPassword");

		Optional<User> user = userService.verifyUserByUsernameAndEmail(username, email);
		String beforePassword = user.get().getPassword();
		if(bCryptPasswordEncoder.matches(newPassword, beforePassword)) {
			return ResponseEntity.status(512).body("* 이전 비밀번호는 사용할 수 없습니다.");
		}
		if(!(bCryptPasswordEncoder.matches(beforePasswordInput, beforePassword))) {
			return ResponseEntity.status(514).body("* 현재 비밀번호가 일치하지 않습니다.");
		}

		boolean isUpdated = userService.updatePassword(username, email, newPassword);

		if (isUpdated) {
			return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
		} else {
			return ResponseEntity.status(513).body("비밀번호 변경에 실패하였습니다.");
		}
	}

	@GetMapping("/profile-image")
	public ResponseEntity<?> getProfileImage(@RequestParam("email") String email) {
		Optional<User> user = userService.findByEmail(email);

		if (user.isPresent() && user.get().getProfileImage() != null) {
			byte[] profileImage = user.get().getProfileImage();
			String base64Image = Base64.getEncoder().encodeToString(profileImage);
			return ResponseEntity.ok("data:image/jpeg;base64," + base64Image);
		} else if (user.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("프로필 사진이 없습니다.");
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다.");
		}
	}


	@PostMapping("/paymentCheck")
	public ResponseEntity<?> paymentCheck(
			@RequestParam("orderId") String orderId,
			@RequestParam("paymentKey") String paymentKey,
			@RequestParam("amount") String amount,
			@RequestParam("id") String id) throws IOException, InterruptedException {
		String toEncode = tossSecret + ":";
		String encodedString = Base64.getEncoder().encodeToString(toEncode.getBytes());
		// HTTP 요청 작성
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("https://api.tosspayments.com/v1/payments/confirm"))
				.header("Authorization", "Basic " + encodedString)
				.header("Content-Type", "application/json")
				.method("POST", HttpRequest.BodyPublishers.ofString(
						"{\"paymentKey\":\"" + paymentKey + "\",\"amount\":" + amount + ",\"orderId\":\"" + orderId + "\"}"
						))
				.build();

		// HTTP 요청 보내기
		HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
		// 상태 코드 확인
		int statusCode = response.statusCode();
		if (statusCode >= 400) {
			// 에러 응답이면 바로 반환
			return ResponseEntity.status(statusCode).body("응답 상태 코드: " + statusCode + ", 오류: " + response.body());
		}
		// JSON 객체로 변환
		JSONObject jsonObject = new JSONObject(response.body());
		// 필요한 값들을 추출
		Integer totalAmount = jsonObject.getInt("totalAmount");
		String orderName = jsonObject.getString("orderName");
		String method = jsonObject.getString("method");
		String status = jsonObject.getString("status");
		String lastTransactionKey = jsonObject.getString("lastTransactionKey");
		String cancels = String.valueOf(jsonObject.get("cancels")); // null 값일 수 있음
		String secret = jsonObject.getString("secret");
		String requestedAt = jsonObject.getString("requestedAt");
		String approvedAt = jsonObject.getString("approvedAt");
		Long changedId = Long.parseLong(id);


		// InputPayment 객체 생성
		InputPayment inputPayment = new InputPayment(
				totalAmount,
				orderId,
				paymentKey,
				orderName,
				method,
				status,
				lastTransactionKey,
				cancels,
				secret,
				requestedAt,
				approvedAt,
				changedId
				);
		PaymentInfo newPaymentInfo;
		// orderName에 따라 다른 메서드 호출
		if ("베이직플랜".equals(orderName)) {
			newPaymentInfo = paymentService.paymentBasicInputUser(inputPayment);
		} else if ("프리미엄플랜".equals(orderName)) {
			newPaymentInfo = paymentService.paymentPremiumInputUser(inputPayment);
		} else {
			// 기본 처리 또는 예외 처리 (필요하다면)
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("유효하지 않은 orderName입니다.");
		}
		// 응답 상태 코드 반환
		return ResponseEntity.status(statusCode).body(newPaymentInfo);
	}

	//회원 수 조회 엔드포인트 추가
	@GetMapping("/users/count")
	public ResponseEntity<Long> getUsersCount(){
		Long userCount = userRepository.count();
		return ResponseEntity.ok(userCount);//회원 수 반환
	}

	@PostMapping("/paymentCancel")
	public ResponseEntity<?> paymentCancel(
			@RequestParam("paymentKey") String paymentKey, @RequestParam("cancelReason") String cancelReason) {
		try {

			String toEncode = tossSecret + ":";
			String encodedString = Base64.getEncoder().encodeToString(toEncode.getBytes());

			// JSON 본문 생성
			String requestBody = String.format("{\"cancelReason\":\"%s\"}", cancelReason);

			// HTTP 요청 작성
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create("https://api.tosspayments.com/v1/payments/" + paymentKey + "/cancel"))
					.header("Authorization", "Basic " + encodedString)
					.header("Content-Type", "application/json")
					.method("POST", HttpRequest.BodyPublishers.ofString(requestBody))
					.build();

			HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

			// 응답 JSON 파싱
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonResponse = objectMapper.readTree(response.body());

			// 응답에서 필요한 값 추출
			String cancelReasonResponse = jsonResponse.at("/cancels/0/cancelReason").asText();
			String canceledAtResponse = jsonResponse.at("/cancels/0/canceledAt").asText();
			String cancelStatusResponse = jsonResponse.at("/cancels/0/cancelStatus").asText();

			// LocalDateTime 변환
			LocalDateTime canceledAt = LocalDateTime.parse(canceledAtResponse, DateTimeFormatter.ISO_OFFSET_DATE_TIME);

			// 데이터베이스 업데이트
			PaymentInfo paymentInfo = paymentRepository.findByPaymentKey(paymentKey);
			if (paymentInfo != null) {
				paymentInfo.setIsCanceled(1);
				paymentInfo.setCancelReason(cancelReasonResponse);
				paymentInfo.setCanceledAt(canceledAt);
				paymentInfo.setCancelStatus(cancelStatusResponse);

				paymentRepository.save(paymentInfo);
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("PaymentInfo not found for paymentKey: " + paymentKey);
			}

			// 상태 코드 추출
			int statusCode = response.statusCode();

			if (statusCode == 200) {
				return ResponseEntity.ok().body("취소가 성공적으로 처리되었습니다.");
			} else {
				return ResponseEntity.status(statusCode).body("취소 처리 실패. 상태 코드: " + statusCode);
			}
		} catch (Exception e) {
			// 예외 발생 시 오류 메시지와 함께 500 응답 반환
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("오류 발생: " + e.getMessage());
		}
	}
	// 회원 비활성화 엔드포인트
	@PostMapping("/deactivateUser")
	public ResponseEntity<?> deactivateUser(@RequestParam("email") String email) {
	    Optional<User> userOptional = userRepository.findByEmail(email);
	    
	    if (userOptional.isEmpty()) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다.");
	    }

	    User user = userOptional.get();
	    user.setIsActivated(0); // 비활성화
	    userRepository.save(user);

	    return ResponseEntity.ok("사용자가 성공적으로 비활성화되었습니다.");
	}
	// 회원 활성화 엔드포인트
	@PostMapping("/activateUser")
	public ResponseEntity<?> activateUser(@RequestParam("email") String email) {
	    Optional<User> userOptional = userRepository.findByEmail(email);
	    
	    if (userOptional.isEmpty()) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다.");
	    }

	    User user = userOptional.get();
	    user.setIsActivated(1); // 활성화
	    userRepository.save(user);

	    return ResponseEntity.ok("사용자가 성공적으로 활성화되었습니다.");
	}
	
	//회원 탈퇴 엔드포인트(탈퇴 시간까지)
	@PostMapping("/deleteUser")
	public ResponseEntity<?> deleteUser(@RequestParam("email") String email) {
	    Optional<User> userOptional = userRepository.findByEmail(email);
	    
	    if (userOptional.isEmpty()) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다.");
	    }

	    User user = userOptional.get();
	    user.setIsDeleted(1); // 탈퇴 처리
	    user.setDeletedTime(LocalDateTime.now()); // 탈퇴 시간 기록
	    userRepository.save(user);

	    return ResponseEntity.ok("사용자가 성공적으로 탈퇴되었습니다.");
	}


}
package com.ictedu.user.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.ictedu.security.jwt.util.JWTUtil;
import com.ictedu.security.model.entity.RefreshToken;
import com.ictedu.security.service.RefreshService;
import com.ictedu.user.model.entity.User;
import com.ictedu.user.repository.UserRepository;
import com.ictedu.user.service.UserService;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/login")
@Slf4j
public class AuthController {
	
	//카카오 로그인 값
    @Value("${kakaoLogin.client.id}")
    private String kakaoClientId;
    
    @Value("${kakaoLogin.redirect.uri}")
    private String kakaoRedirectUri;
    
    //네이버 로그인 값
    @Value("${naverLogin.client.id}")
    private String naverClientId;
    
    @Value("${naverLogin.client.secret}")
    private String naverClientSecret;
   
    
    private final UserRepository userRepository;
    private final UserService userService;
    private final RefreshService refreshService;
    private final JWTUtil jwtUtil;

    
    public AuthController(UserRepository userRepository, UserService userService, RefreshService refreshService, AuthenticationManager authenticationManager, JWTUtil jwtUtil) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.refreshService = refreshService;
        this.jwtUtil = jwtUtil;
    }
    
    @GetMapping("/naver/callback")
    public void naverLogin(@RequestParam String code, @RequestParam String state, HttpServletResponse response) throws IOException, JSONException {
    	log.info("code = {}", code);
    	
        // 액세스 토큰을 요청하기 위한 URL 및 헤더 설정
        String tokenUrl = "https://nid.naver.com/oauth2.0/token";
        HttpHeaders tokenHeaders = new HttpHeaders();
        tokenHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        String tokenRequestBody = "grant_type=authorization_code"
                + "&client_id=" + naverClientId
                + "&client_secret=" + naverClientSecret
                + "&state=" + state
                + "&code=" + code;
        
        // 토큰 요청
        HttpEntity<String> tokenRequestEntity = new HttpEntity<>(tokenRequestBody, tokenHeaders);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> tokenResponse = restTemplate.exchange(tokenUrl, HttpMethod.POST, tokenRequestEntity, String.class);
        log.info("token response = {}", tokenResponse.getBody());
        
        JSONObject tokenJson = new JSONObject(tokenResponse.getBody());
        String accessToken = tokenJson.getString("access_token");
        log.info("accessToken = {}", accessToken);
        
        // 사용자 정보를 요청하기 위한 URL 및 헤더 설정
        String userInfoUrl = "https://openapi.naver.com/v1/nid/me";
        HttpHeaders userInfoHeaders = new HttpHeaders();
        userInfoHeaders.set("Authorization", "Bearer " + accessToken);

        // 사용자 정보 요청
        HttpEntity<String> userInfoRequestEntity = new HttpEntity<>(userInfoHeaders);
        ResponseEntity<String> userInfoResponse = restTemplate.exchange(userInfoUrl, HttpMethod.GET, userInfoRequestEntity, String.class);
        log.info("user info response = {}", userInfoResponse.getBody());

        JSONObject userJson = new JSONObject(userInfoResponse.getBody());
        
        //Email 처리
        String email = userJson.getJSONObject("response").has("email") ?
                userJson.getJSONObject("response").getString("email") : "이메일 정보가 없습니다.";
        
        //username 처리
        String name = userJson.getJSONObject("response").has("name") ?
                userJson.getJSONObject("response").getString("name") : "이름 정보가 없습니다.";
        
        // Birth 처리
        String birthyear = userJson.getJSONObject("response").has("birthyear") ?
                userJson.getJSONObject("response").getString("birthyear") : "출생년도 정보가 없습니다.";
        String birthday = userJson.getJSONObject("response").has("birthday") ?
                userJson.getJSONObject("response").getString("birthday") : "생일 정보가 없습니다.";
        String birth = "출생년도 정보가 없습니다.".equals(birthyear) || "생일 정보가 없습니다.".equals(birthday) ?
                "출생일 정보가 없습니다." : birthyear + "-" + birthday.substring(0, 2) + "-" + birthday.substring(3);
        
        // Phone 처리
        String phone = userJson.getJSONObject("response").has("mobile_e164") ?
                userJson.getJSONObject("response").getString("mobile_e164") : "핸드폰 정보가 없습니다.";
        phone = phone.replace("+82", "0").replace("-", "");
        
        // Gender 처리
        String gender = userJson.getJSONObject("response").has("gender") ?
                userJson.getJSONObject("response").getString("gender") : "성별 정보가 없습니다.";
        switch (gender) {
            case "M":
                gender = "men";
                break;
            case "F":
                gender = "women";
                break;
            default:
                gender = null; // 'U' 또는 다른 값은 null로 처리
                break;
        }

        // Profile Image 처리
        String profileImageUrl = userJson.getJSONObject("response").has("profile_image")?
        		userJson.getJSONObject("response").getString("profile_image") : "프로필 정보가 없습니다.";
        byte[] profileImage = null;
        try (InputStream in = new URL(profileImageUrl).openStream()) {
            profileImage = in.readAllBytes();
        } catch (IOException e) {
            log.error("Failed to download profile image", e);
        }
        
        Optional<User> optionalUser = userRepository.findByEmailAndIsNaver(email, 1);
        
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setLastLogin(LocalDateTime.now());
            user.setSnsAccessToken(accessToken);
            userRepository.save(user);

            // JWT 토큰 발급
            Long accessExpiredMs = 600000L;
            String accessTokenJwt = jwtUtil.generateToken(email, "access", accessExpiredMs);
            Long refreshExpiredMs = 86400000L;
            String refreshTokenJwt = jwtUtil.generateToken(email, "refresh", refreshExpiredMs);

            RefreshToken refreshToken = RefreshToken.builder()
                    .status("activated")
                    .userAgent(response.getHeader("User-Agent"))
                    .user(user)
                    .tokenValue(refreshTokenJwt)
                    .expiresTime(refreshExpiredMs)
                    .build();

            refreshService.save(refreshToken);

            // 로그인 성공 후 URL에 토큰 정보 포함
            Optional<User> optinalUserForId = userRepository.findByEmailAndIsNaver(email, 1);
            Long id = optinalUserForId.get().getId();
            String encodedUsername = URLEncoder.encode(name, StandardCharsets.UTF_8.toString());
            
            String redirectUrl = String.format("http://localhost:3000/auth/successSocialLogin?access=%s&refresh=%s&isAdmin=%s&id=%s&gender=%s&username=%s&email=%s&birth=%s&phone=%s&profile=%s",
                    accessTokenJwt, refreshTokenJwt, optinalUserForId.get().getIsAdmin(), id, gender, encodedUsername, email, birth, phone, profileImageUrl);

            response.sendRedirect(redirectUrl);
            log.info("로그인 성공: {}", email);
        } else {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setUsername(name);
            newUser.setBirth(birth);
            newUser.setPhone(phone);
            newUser.setProfileImage(profileImage);
            newUser.setIsKakao(0);
            newUser.setCreatedTime(LocalDateTime.now());
            newUser.setIsDeleted(0);
            newUser.setIsActivated(1);
            newUser.setIsEmailVerified(1);
            newUser.setIsAdmin(0);
            newUser.setIsGoogle(0);
            newUser.setIsNaver(1);
            newUser.setPassword("");
            userRepository.save(newUser);
            log.info("회원가입 성공: {}", email);

            // 네이버 로그아웃 처리
            String logoutUrl = "https://nid.naver.com/nidlogin.logout"; // 네이버 로그아웃 URL
            HttpHeaders logoutHeaders = new HttpHeaders();
            logoutHeaders.set("Authorization", "Bearer " + accessToken);
            HttpEntity<String> logoutRequestEntity = new HttpEntity<>(logoutHeaders);
            ResponseEntity<String> logoutResponse = restTemplate.exchange(logoutUrl, HttpMethod.GET, logoutRequestEntity, String.class);
            
            // JWT 토큰 발급
            Long accessExpiredMs = 600000L;
            String accessTokenJwt = jwtUtil.generateToken(email, "access", accessExpiredMs);
            Long refreshExpiredMs = 86400000L;
            String refreshTokenJwt = jwtUtil.generateToken(email, "refresh", refreshExpiredMs);

            RefreshToken refreshToken = RefreshToken.builder()
                    .status("activated")
                    .userAgent(response.getHeader("User-Agent"))
                    .user(newUser)
                    .tokenValue(refreshTokenJwt)
                    .expiresTime(refreshExpiredMs)
                    .build();

            refreshService.save(refreshToken);
            
            Optional<User> optinalUserForId = userRepository.findByEmailAndIsNaver(email, 1);
            Long id = optinalUserForId.get().getId();
            String encodedUsername = URLEncoder.encode(name, StandardCharsets.UTF_8.toString());
            
            String redirectUrl = String.format("http://localhost:3000/auth/successSocialLogin?access=%s&refresh=%s&isAdmin=%s&id=%s&gender=%s&username=%s&email=%s&birth=%s&phone=%s&profile=%s",
                    accessTokenJwt, refreshTokenJwt, optinalUserForId.get().getIsAdmin(), id, gender, encodedUsername, email, birth, phone, profileImageUrl);

            response.sendRedirect(redirectUrl);
        }
    }
    
    //////////////////////////////////////////////////////////////////////////////////////
    
    @GetMapping("/kakao/callback")
    public void kakaoLogin(@RequestParam String code, HttpServletResponse response) throws IOException, JSONException {
        log.info("code = {}", code);

        // 액세스 토큰을 요청하기 위한 URL 및 헤더 설정
        String tokenUrl = "https://kauth.kakao.com/oauth/token";
        HttpHeaders tokenHeaders = new HttpHeaders();
        tokenHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        String tokenRequestBody = "grant_type=authorization_code"
                + "&client_id=" + kakaoClientId
                + "&redirect_uri=" + kakaoRedirectUri
                + "&code=" + code;

        // 토큰 요청
        HttpEntity<String> tokenRequestEntity = new HttpEntity<>(tokenRequestBody, tokenHeaders);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> tokenResponse = restTemplate.exchange(tokenUrl, HttpMethod.POST, tokenRequestEntity, String.class);
        log.info("token response = {}", tokenResponse.getBody());
        
        JSONObject tokenJson = new JSONObject(tokenResponse.getBody());
        String accessToken = tokenJson.getString("access_token");
        log.info("accessToken = {}", accessToken);

        // 사용자 정보를 요청하기 위한 URL 및 헤더 설정
        String userInfoUrl = "https://kapi.kakao.com/v2/user/me";
        HttpHeaders userInfoHeaders = new HttpHeaders();
        userInfoHeaders.set("Authorization", "Bearer " + accessToken);

        // 사용자 정보 요청
        HttpEntity<String> userInfoRequestEntity = new HttpEntity<>(userInfoHeaders);
        ResponseEntity<String> userInfoResponse = restTemplate.exchange(userInfoUrl, HttpMethod.GET, userInfoRequestEntity, String.class);
        log.info("user info response = {}", userInfoResponse.getBody());

        JSONObject userJson = new JSONObject(userInfoResponse.getBody());

        //Email 처리
        String email = userJson.getJSONObject("kakao_account").has("email") ?
                userJson.getJSONObject("kakao_account").getString("email") : "이메일 정보가 없습니다.";
        
        //username 처리
        String name = userJson.getJSONObject("kakao_account").has("name") ?
                userJson.getJSONObject("kakao_account").getString("name") : "이름 정보가 없습니다.";
        
        // Birth 처리
        String birthyear = userJson.getJSONObject("kakao_account").has("birthyear") ?
                userJson.getJSONObject("kakao_account").getString("birthyear") : "출생년도 정보가 없습니다.";
        String birthday = userJson.getJSONObject("kakao_account").has("birthday")?
        		userJson.getJSONObject("kakao_account").getString("birthday") : "생일 정보가 없습니다.";
        String birth = birthyear + "-" + birthday.substring(0, 2) + "-" + birthday.substring(2);

        // Phone 처리
        String phone = userJson.getJSONObject("kakao_account").has("phone_number")?
        		userJson.getJSONObject("kakao_account").getString("phone_number") : "핸드폰 정보가 없습니다.";
        phone = phone.replace("+82 ", "0").replace("-", "");
        
        //Gender 처리
        String gender = userJson.getJSONObject("kakao_account").has("gender")?
        		userJson.getJSONObject("kakao_account").getString("gender") : "성별 정보가 없습니다.";
        gender = gender.equals("male") ? "men" : "women";

        // Profile Image 처리
        String profileImageUrl = userJson.getJSONObject("kakao_account").has("profile")?
        		userJson.getJSONObject("kakao_account").getJSONObject("profile").getString("profile_image_url") : "프로필 정보가 없습니다.";
        byte[] profileImage = null;
        try (InputStream in = new URL(profileImageUrl).openStream()) {
            profileImage = in.readAllBytes();
        } catch (IOException e) {
            log.error("Failed to download profile image", e);
        }
        
        Optional<User> optionalUser = userRepository.findByEmailAndIsKakao(email, 1);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setLastLogin(LocalDateTime.now());
            user.setSnsAccessToken(accessToken);
            userRepository.save(user);

            // JWT 토큰 발급
            Long accessExpiredMs = 600000L;
            String accessTokenJwt = jwtUtil.generateToken(email, "access", accessExpiredMs);
            Long refreshExpiredMs = 86400000L;
            String refreshTokenJwt = jwtUtil.generateToken(email, "refresh", refreshExpiredMs);

            RefreshToken refreshToken = RefreshToken.builder()
                    .status("activated")
                    .userAgent(response.getHeader("User-Agent"))
                    .user(user)
                    .tokenValue(refreshTokenJwt)
                    .expiresTime(refreshExpiredMs)
                    .build();

            refreshService.save(refreshToken);

            // 로그인 성공 후 URL에 토큰 정보 포함
            Optional<User> optinalUserForId = userRepository.findByEmailAndIsKakao(email, 1);
            Long id = optinalUserForId.get().getId();
            String encodedUsername = URLEncoder.encode(name, StandardCharsets.UTF_8.toString());
            System.out.println("카카오에서 로그인한 사용자의 id값: "+ id);
            
            String redirectUrl = String.format("http://localhost:3000/auth/successSocialLogin?access=%s&refresh=%s&isAdmin=%s&id=%s&gender=%s&username=%s&email=%s&birth=%s&phone=%s&profile=%s",
                    accessTokenJwt, refreshTokenJwt, optinalUserForId.get().getIsAdmin(), id, gender, encodedUsername, email, birth, phone, profileImageUrl);

            response.sendRedirect(redirectUrl);
            log.info("로그인 성공: {}", email);
        } else {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setUsername(name);
            newUser.setBirth(birth);
            newUser.setPhone(phone);
            newUser.setProfileImage(profileImage);
            newUser.setIsKakao(1);
            newUser.setCreatedTime(LocalDateTime.now());
            newUser.setIsDeleted(0);
            newUser.setIsActivated(1);
            newUser.setIsEmailVerified(1);
            newUser.setIsAdmin(0);
            newUser.setIsGoogle(0);
            newUser.setIsNaver(0);
            newUser.setPassword("");
            userRepository.save(newUser);
            log.info("회원가입 성공: {}", email);

            // 카카오 로그아웃 처리
            String logoutUrl = "https://kapi.kakao.com/v1/user/logout";
            HttpHeaders logoutHeaders = new HttpHeaders();
            logoutHeaders.set("Authorization", "Bearer " + accessToken);

            HttpEntity<String> logoutRequestEntity = new HttpEntity<>(logoutHeaders);
            ResponseEntity<String> logoutResponse = restTemplate.exchange(logoutUrl, HttpMethod.POST, logoutRequestEntity, String.class);
            log.info("logout response = {}", logoutResponse.getBody());
            
            // JWT 토큰 발급
            Long accessExpiredMs = 600000L;
            String accessTokenJwt = jwtUtil.generateToken(email, "access", accessExpiredMs);
            Long refreshExpiredMs = 86400000L;
            String refreshTokenJwt = jwtUtil.generateToken(email, "refresh", refreshExpiredMs);

            RefreshToken refreshToken = RefreshToken.builder()
                    .status("activated")
                    .userAgent(response.getHeader("User-Agent"))
                    .user(newUser)
                    .tokenValue(refreshTokenJwt)
                    .expiresTime(refreshExpiredMs)
                    .build();

            refreshService.save(refreshToken);
            
            Optional<User> optinalUserForId = userRepository.findByEmailAndIsKakao(email, 1);
            Long id = optinalUserForId.get().getId();
            String encodedUsername = URLEncoder.encode(name, StandardCharsets.UTF_8.toString());
            
            String redirectUrl = String.format("http://localhost:3000/auth/successSocialLogin?access=%s&refresh=%s&isAdmin=%s&id=%s&gender=%s&username=%s&email=%s&birth=%s&phone=%s&profile=%s",
                    accessTokenJwt, refreshTokenJwt, optinalUserForId.get().getIsAdmin(), id, gender, encodedUsername, email, birth, phone, profileImageUrl);

            response.sendRedirect(redirectUrl);
        }
    }
    
}

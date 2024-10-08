package com.ictedu.security.handler;

import java.io.IOException;
import java.util.Optional;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.web.client.RestTemplate;

import com.ictedu.security.jwt.util.JWTUtil;
import com.ictedu.security.model.entity.RefreshToken;
import com.ictedu.security.service.RefreshService;
import com.ictedu.user.model.entity.User;
import com.ictedu.user.service.UserService;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomLogoutHandler implements LogoutHandler {
    private final JWTUtil jwtUtil;
    private final RefreshService refreshService;
    private final UserService userService;

    public CustomLogoutHandler(JWTUtil jwtUtil, RefreshService refreshService, UserService userService) {
        this.jwtUtil = jwtUtil;
        this.refreshService = refreshService;
        this.userService = userService;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.startsWith("Bearer ")) {
            String token = authorization.substring(7); // 'Bearer ' 문자 제거

            try {
                jwtUtil.isTokenExpired(token);
            } catch (ExpiredJwtException e) {
                log.info("Token expired during logout: {}", e.getMessage());
                // HTTP 응답을 설정하여 직접 클라이언트에게 오류 정보를 전달합니다.
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
                response.setContentType("application/json");
                try {
                    response.getWriter().write("{\"error\":\"Session has expired. Please log in again.\"}");
                    response.getWriter().flush();
                } catch (IOException ioException) {
                    log.error("Error writing to response", ioException);
                }
                return; // 메서드를 빠져나와 추가 처리를 중단합니다.
            }

            // 만료 여부와 상관없이 사용자 정보를 조회하여 로그아웃 처리를 합니다.
            String userName = jwtUtil.getUserEmailFromToken(token);
            Optional<User> userOptional = userService.findByEmail(userName);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                Optional<RefreshToken> refresh = refreshService.findByUserId(user.getId());
                refresh.ifPresent(refreshToken -> refreshService.deleteByRefresh(refreshToken.getTokenValue()));
            }
        }
        // 성공적인 로그아웃 응답을 설정합니다.
        response.setStatus(HttpServletResponse.SC_OK);
    }
}

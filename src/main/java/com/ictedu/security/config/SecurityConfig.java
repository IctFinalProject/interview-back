package com.ictedu.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.ictedu.security.handler.CustomLogoutHandler;
import com.ictedu.security.jwt.filter.JWTFilter;
import com.ictedu.security.jwt.filter.LoginFilter;
import com.ictedu.security.jwt.util.JWTUtil;
import com.ictedu.security.service.RefreshService;
import com.ictedu.user.service.UserService;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final UserService userService;
    private final RefreshService refreshService;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTUtil jwtUtil;
	
    public SecurityConfig(UserService userService, RefreshService refreshService, AuthenticationConfiguration authenticationConfiguration, JWTUtil jwtUtil) {
        this.userService = userService;
        this.refreshService = refreshService;
        this.authenticationConfiguration = authenticationConfiguration;
        this.jwtUtil = jwtUtil;
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
	
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .formLogin(formLogin -> formLogin.disable())
                .httpBasic(httpBasic -> httpBasic.disable())
                .authorizeHttpRequests(auth -> auth
                		//테스트 동안은 비활성화 
                        .requestMatchers(HttpMethod.POST, "/admin").hasRole("ADMIN")
                        .requestMatchers("/logout", "/api/auth/*", "/auth", "/login", "/api-docs/**", "/logout", "/reissue", "/api/email/*", "/api/phone/*").permitAll()
                        .anyRequest().permitAll())
                .addFilterBefore(new JWTFilter(jwtUtil), LoginFilter.class)
                .addFilterAt(new LoginFilter(userService, refreshService, authenticationManager(authenticationConfiguration), jwtUtil), UsernamePasswordAuthenticationFilter.class)
                .logout(logout -> logout
                        .addLogoutHandler(new CustomLogoutHandler(jwtUtil, refreshService, userService))
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setStatus(HttpServletResponse.SC_OK);
                        }))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
    
}
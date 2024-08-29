package com.ictedu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("classpath:application.yml")
@PropertySource(value = "file:.env", ignoreResourceNotFound = true)
public class BackendApplication{
	// 메인 애플리케이션 클래스
    // application.yml과 .env 파일을 프로퍼티 소스로 사용
	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}
}
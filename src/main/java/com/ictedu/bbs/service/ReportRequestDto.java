package com.ictedu.bbs.service;

import java.util.Map;

import lombok.Data;

@Data
public class ReportRequestDto {
    private Long postId;
    private String reason;
    private Map<String, Boolean> additionalInfo;  // Map 또는 Object로 처리
}


package com.ictedu.interview.model.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionDTO {
	private Long id;
    private String questionText;
    private String questionType;
    private String script;
    private String keywords;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}

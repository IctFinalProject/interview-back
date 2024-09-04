package com.ictedu.interview.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "question_type")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionType {

	@Id
	@SequenceGenerator(name="seq_interview_questiontype_id",sequenceName = "seq_interview_questiontype_id",allocationSize = 1,initialValue = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "seq_interview_questiontype_id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;
}

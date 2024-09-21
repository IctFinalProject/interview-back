package com.ictedu.resume.entity;

import java.time.LocalDateTime;

import com.ictedu.user.model.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "resume")

public class ResumeEntity {
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
	
    @Id
    @SequenceGenerator(name = "resume_seq", sequenceName = "resume_seq", allocationSize = 1, initialValue = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "resume_seq")
    @Column(name = "resume_id")
    private Long resumeId;

    @Lob
    @Column(name = "resume_pdf", nullable = false)
    private byte[] resumePdf;
    
    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;
    
    @Column(name = "title", nullable = false)
    private String title;
    
    @Column(name = "keywords_self_introduction")
    private String keywordsSelfIntroduction;
    

    @Column(name = "keywords_motivation")
    private String keywordsMotivation;
    
    @Column(name = "desired_company")
    private String desiredCompany;
}

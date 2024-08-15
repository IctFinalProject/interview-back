package com.ictedu.resume.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "resume")
public class ResumeEntity {
	
	@Id
	@SequenceGenerator(name="seq_resumeId",sequenceName = "seq_resumeId",allocationSize = 1,initialValue = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "seq_resumeId")
    private Long resumeId;
    
    @Column(name="resume_title", nullable = true)
    private String resumeTitle;

    @Column(name="school_name", nullable = true)
    private String schoolName;

    @Column(name="major", nullable = true)
    private String major;

    @Column(name="start_date", nullable = true)
    private String startDate;

    @Column(name="end_date", nullable = true)
    private String endDate;

    @Column(name="graduation_status", nullable = true)
    private String graduationStatus;

    @Column(name="company_name", nullable = true)
    private String companyName;

    @Column(name="join_date", nullable = true)
    private String joinDate;

    @Column(name="leave_date", nullable = true)
    private String leaveDate;

    @Column(name="position", nullable = true)
    private String position;

    @Column(name="job_description", nullable = true)
    private String jobDescription;

    @Column(name="language", nullable = true)
    private String language;

    @Column(name="language_level", nullable = true)
    private String languageLevel;

    @Column(name="language_score", nullable = true)
    private String languageScore;

    @Column(name="contest_name", nullable = true)
    private String contestName;

    @Column(name="contest_award", nullable = true)
    private String contestAward;

    @Column(name="contest_date", nullable = true)
    private String contestDate;

    @Column(name="certificate_name", nullable = true)
    private String certificateName;

    @Column(name="certificate_issuer", nullable = true)
    private String certificateIssuer;

    @Column(name="certificate_date", nullable = true)
    private String certificateDate;

    @Column(name="portfolio_description", nullable = true)
    private String portfolioDescription;

    @Column(name="military_service_type", nullable = true)
    private String militaryServiceType;

    @Column(name="military_start_date", nullable = true)
    private String militaryStartDate;

    @Column(name="military_end_date", nullable = true)
    private String militaryEndDate;

    @Column(name="military_rank", nullable = true)
    private String militaryRank;

    @Column(name="desired_salary", nullable = true)
    private String desiredSalary;

    @Column(name="desired_start_date", nullable = true)
    private String desiredStartDate;

    @Column(name="self_introduction", nullable = true)
    private String selfIntroduction;
    
    @Column(name="motivation", nullable = true)
    private String motivation;
    
    @Column(name="gender", nullable = true)
    private String gender;
}

package com.ictedu.resume.entity;

import com.ictedu.user.model.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "resume_proofread")
public class ResumeProofreadEntity {

    @Id
    @SequenceGenerator(name = "proofread_seq", sequenceName = "proofread_seq", allocationSize = 1, initialValue = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "proofread_seq")
    @Column(name = "proofread_id")
    private Long proofreadId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id", nullable = false)
    private ResumeEntity resume;

    @Lob
    @Column(name = "self_introduction", nullable = false)
    private String selfIntroduction;

    @Lob
    @Column(name = "motivation", nullable = false)
    private String motivation;
}

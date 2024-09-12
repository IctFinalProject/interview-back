package com.ictedu.interview.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "video_speech_analysis")
public class VideoSpeechAnalysis {

	@Id
    @SequenceGenerator(name="seq_video_speech_analysis_id", sequenceName = "seq_video_speech_analysis_id", allocationSize = 1, initialValue = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_video_speech_analysis_id")
    private Long id;

	@ManyToOne
	@JoinColumn(name = "video_id", referencedColumnName = "id", nullable = false)
	private VideoEntity video;

    @Lob
    @Column(columnDefinition = "CLOB")
    private String transcription;

    @Column(name = "sentiment_overall")
    private String sentimentOverall;

    @Column(name = "sentiment_confidence")
    private Double sentimentConfidence;

    @Lob
    @Column(columnDefinition = "CLOB")
    private String summary;

    @ColumnDefault("SYSDATE")
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;


}

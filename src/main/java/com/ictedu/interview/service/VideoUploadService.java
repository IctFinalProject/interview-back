package com.ictedu.interview.service;

import com.google.auth.oauth2.GoogleCredentials;

import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import com.ictedu.interview.model.entity.VideoEntity;
import com.ictedu.interview.repository.VideoRepository;
import com.ictedu.resume.entity.ResumeEntity;
import com.ictedu.resume.service.ResumeService;
import jakarta.persistence.EntityNotFoundException;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class VideoUploadService {

    private static final Logger logger = LoggerFactory.getLogger(VideoUploadService.class);

    @Value("${spring.cloud.storage.bucket}")
    private String bucketName;

    @Value("${spring.cloud.gcp.credentials.location}")
    private String credentialsPath;

    @Autowired
    private VideoRepository videoRepository;
    
    @Autowired
    private final ResumeService resumeService;

    private final Storage storage;
    private static final long MAX_FILE_SIZE = 100 * 1024 * 1024; // 100MB
    private static final String[] ALLOWED_EXTENSIONS = {".mp4", ".avi", ".mov"};

    @Autowired
    public VideoUploadService(ResourceLoader resourceLoader, @Value("${spring.cloud.gcp.credentials.location}") String credentialsPath) throws IOException {
        // GCP 인증 파일 로드
        Resource resource = resourceLoader.getResource(credentialsPath);
        if (!resource.exists()) {
            throw new IllegalArgumentException("Credentials file not found at " + credentialsPath);
        }
		this.resumeService = new ResumeService();
        try (InputStream keyFile = resource.getInputStream()) {
            GoogleCredentials credentials = GoogleCredentials.fromStream(keyFile);
            this.storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
        }
    }

    public VideoEntity processVideo(MultipartFile video, Long userId, Long questionId, String choosedResume, String questionText) throws IOException {
        logger.info("Processing video for user: {}, question: {}", userId, questionId);
        
        validateVideoFile(video);
        String videoLink = uploadToGoogleCloudStorage(video);
        ResumeEntity resumeEntity = getResumeEntity(choosedResume);

        VideoEntity videoEntity = createVideoEntity(video, userId, questionId, questionText, videoLink, resumeEntity);

        VideoEntity savedEntity = videoRepository.save(videoEntity);
        logger.info("Video processed and saved with id: {}", savedEntity.getId());
        return savedEntity;
    }

    private void validateVideoFile(MultipartFile video) throws IOException {
        if (video == null || video.isEmpty()) {
            logger.error("Received empty video file");
            throw new IllegalArgumentException("Video file is empty or null");
        }

        // 파일 크기 검사
        if (video.getSize() > MAX_FILE_SIZE) {
            logger.error("Video file size exceeds maximum allowed size");
            throw new IllegalArgumentException("Video file size exceeds maximum allowed size of " + MAX_FILE_SIZE / (1024 * 1024) + "MB");
        }

        // 파일 확장자 검사
        String originalFilename = video.getOriginalFilename();
        if (originalFilename != null) {
            boolean validExtension = false;
            for (String extension : ALLOWED_EXTENSIONS) {
                if (originalFilename.toLowerCase().endsWith(extension)) {
                    validExtension = true;
                    break;
                }
            }
            if (!validExtension) {
                logger.error("Invalid video file format: {}", originalFilename);
                throw new IllegalArgumentException("Invalid video file format. Allowed formats are: " + String.join(", ", ALLOWED_EXTENSIONS));
            }
        } else {
            logger.error("Video filename is null");
            throw new IllegalArgumentException("Video filename is null");
        }

        String contentType = video.getContentType();
		if (contentType == null || !contentType.startsWith("video/")) {
		    logger.error("Invalid content type for video file: {}", contentType);
		    throw new IllegalArgumentException("Invalid content type for video file. Expected video content.");
		}

        logger.info("Video file validation passed for file: {}", originalFilename);
    }

    private ResumeEntity getResumeEntity(String choosedResume) {
        try {
            Long choosedResumeLong = Long.parseLong(choosedResume);
            Optional<ResumeEntity> getResume = resumeService.findResumeById(choosedResumeLong);
            return getResume.orElseThrow(() -> {
                logger.warn("Resume not found for id: {}", choosedResumeLong);
                return new EntityNotFoundException("Resume not found for id: " + choosedResumeLong);
            });
        } catch (NumberFormatException e) {
            logger.error("Invalid resume ID format: {}", choosedResume);
            throw new IllegalArgumentException("Invalid resume ID format", e);
        }
    }

    private VideoEntity createVideoEntity(MultipartFile video, Long userId, Long questionId, 
            String questionText, String videoLink, ResumeEntity resumeEntity) {
		VideoEntity videoEntity = new VideoEntity();
		videoEntity.setUserId(userId);
		videoEntity.setQuestionId(questionId);
		videoEntity.setQuestionText(questionText);
		videoEntity.setFilePath(videoLink);
		videoEntity.setFileName(video.getOriginalFilename());
		videoEntity.setFileSize(video.getSize());
		videoEntity.setUploadDate(LocalDateTime.now());
		videoEntity.setResume(resumeEntity);  // 여기서 ResumeEntity를 설정합니다.
		return videoEntity;
	}

    private String uploadToGoogleCloudStorage(MultipartFile video) throws IOException {
        // 유니크한 파일명 생성
        String uniqueFileName = UUID.randomUUID() + "_" + StringUtils.cleanPath(video.getOriginalFilename());

        // 파일을 Google Cloud Storage에 업로드
        BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, uniqueFileName)
                .setContentType(video.getContentType())
                .build();
        storage.create(blobInfo, video.getBytes());

        String fileLink = String.format("https://storage.googleapis.com/%s/%s", bucketName, uniqueFileName);
        logger.info("Video uploaded to Google Cloud Storage: {}", fileLink);

        return fileLink;  // GCS 링크 반환
    }
}

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

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
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

    private final Storage storage;

    @Autowired
    public VideoUploadService(ResourceLoader resourceLoader, @Value("${spring.cloud.gcp.credentials.location}") String credentialsPath) throws IOException {
        // GCP 인증 파일 로드
        Resource resource = resourceLoader.getResource(credentialsPath);
        if (!resource.exists()) {
            throw new IllegalArgumentException("Credentials file not found at " + credentialsPath);
        }
        try (InputStream keyFile = resource.getInputStream()) {
            GoogleCredentials credentials = GoogleCredentials.fromStream(keyFile);
            this.storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
        }
    }

    public VideoEntity processVideo(MultipartFile video, Long userId, Long questionId) throws IOException {
        // GCS에 파일 업로드
        String videoLink = uploadToGoogleCloudStorage(video);

        VideoEntity videoEntity = new VideoEntity();
        videoEntity.setUserId(userId);
        videoEntity.setQuestionId(questionId);
        videoEntity.setFilePath(videoLink);  // GCS 링크 저장
        videoEntity.setFileName(video.getOriginalFilename());
        videoEntity.setFileSize(video.getSize());
        videoEntity.setUploadDate(LocalDateTime.now());

        return videoRepository.save(videoEntity);
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

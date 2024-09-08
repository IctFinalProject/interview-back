package com.ictedu.interview.service;

import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.ictedu.interview.model.entity.VideoEntity;
import com.ictedu.interview.repository.VideoRepository;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

@Service
public class VideoUploadService {
    private static final Logger logger = LoggerFactory.getLogger(VideoUploadService.class);


    @Value("${google.drive.credentials}")
    private String credentialsPath;

    private Drive driveService;

    @Autowired
    private VideoRepository videoRepository;

    // Google Drive API 서비스 초기화
    @Autowired
    public VideoUploadService(ResourceLoader resourceLoader, @Value("${google.drive.credentials}") String credentialsPath) throws GeneralSecurityException, IOException {
        // ResourceLoader로 파일 로드
        Resource resource = resourceLoader.getResource(credentialsPath);
        Assert.notNull(resource, "Resource must not be null");  // 경로 확인을 위한 검사

        // InputStream을 통해 credentials.json 로드
        InputStream credentialsStream = resource.getInputStream();
        GoogleCredentials credential = GoogleCredentials.fromStream(credentialsStream)
                .createScoped(Collections.singleton(DriveScopes.DRIVE_FILE));

        // HTTP Transport와 JSON Factory 객체 생성
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

        // Google Drive API 서비스 객체 초기화
        driveService = new Drive.Builder(GoogleNetHttpTransport.newTrustedTransport(), jsonFactory, new HttpCredentialsAdapter(credential))
                .setApplicationName("Video Upload App")
                .build();
    }

    public VideoEntity processVideo(MultipartFile video, Long userId, Long questionId) throws IOException {
        // Google Drive에 파일 업로드
        String videoLink = uploadToGoogleDrive(video);

        VideoEntity videoEntity = new VideoEntity();
        videoEntity.setUserId(userId);
        videoEntity.setQuestionId(questionId);
        videoEntity.setFilePath(videoLink);  // Google Drive 링크 저장
        videoEntity.setFileName(video.getOriginalFilename());
        videoEntity.setFileSize(video.getSize());
        videoEntity.setUploadDate(LocalDateTime.now());

        return videoRepository.save(videoEntity);
    }

    private String uploadToGoogleDrive(MultipartFile video) throws IOException {
        // 유니크한 파일명 생성
        String uniqueFileName = UUID.randomUUID() + "_" + StringUtils.cleanPath(video.getOriginalFilename());

        // 파일을 임시 폴더에 저장
        java.io.File tempFile = java.io.File.createTempFile("temp", uniqueFileName);
        video.transferTo(tempFile);

        // Google Drive 메타데이터 설정
        File fileMetadata = new File();
        fileMetadata.setName(uniqueFileName);

        // 파일을 Google Drive에 업로드
        FileContent mediaContent = new FileContent("video/mp4", tempFile);
        File file = driveService.files().create(fileMetadata, mediaContent)
                .setFields("id, webContentLink")
                .execute();

        logger.info("Video uploaded to Google Drive: {}", file.getWebContentLink());

        // Google Drive 링크 반환
        return file.getWebContentLink();
    }

//    private String saveVideo(MultipartFile video) throws IOException {
//        // 1. 업로드 디렉토리 경로 설정
//        Path uploadPath = Paths.get(uploadDir);
//
//        // 2. 디렉토리가 존재하지 않으면 생성
//        if (!Files.exists(uploadPath)) {
//            Files.createDirectories(uploadPath);
//        }
//
//        // 3. 유니크한 파일명 생성
//        String filename = UUID.randomUUID() + "_" + StringUtils.cleanPath(video.getOriginalFilename());
//
//        // 4. 전체 파일 경로 설정
//        Path filePath = uploadPath.resolve(filename);
//
//        // 5. 파일 저장
//        Files.copy(video.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
//
//        // 6. 상대 경로 반환
//        return uploadPath.relativize(filePath).toString();
//    }
}

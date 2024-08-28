package com.ictedu.bbs.service;

import org.springframework.beans.factory.annotation.Autowired;




import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;

import com.ictedu.bbs.model.entity.Bbs;
import com.ictedu.bbs.model.entity.FileBbs;
import com.ictedu.bbs.repository.BbsRepository;
import com.ictedu.bbs.repository.FileBbsRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BbsService {

	public final BbsRepository bbsRepository;
	public final FileBbsRepository fileBbsRepository;
	private List<MultipartFile> files;

	@Autowired
	public BbsService(BbsRepository bbsRepository, FileBbsRepository fileBbsRepository) {
		this.bbsRepository = bbsRepository;
		this.fileBbsRepository = fileBbsRepository;
	}


	public List<Bbs> findAll() {
		return bbsRepository.findAll();
	}

	public Optional<Bbs> findById(Long id) {
		return bbsRepository.findById(id);
	}

	@Transactional
    public Bbs insertBbs(BbsDto bbsDto) {
        // 게시글 엔티티 생성
        Bbs bbs = inputBbs(bbsDto);

        // 게시글 저장
        Bbs savedBbs = bbsRepository.save(bbs);
        /*
        // 파일 첨부 처리
        if (files != null && !files.isEmpty()) {
            saveFiles(savedBbs, files);
        }
		*/
        return savedBbs;
    }

	public Bbs inputBbs(BbsDto bbsDto) {
		//return bbsRepository.save(bbs);
		return Bbs.builder()
				.title(bbsDto.getTitle())
				.content(bbsDto.getContent())
				.hitCount(0L)
				.active(1)
				.inactive(0)
				.reported(0)
				.deleted(0)
				.edited(0)
				.type("normal")
				.userId(bbsDto.getUserId())
				.build();
	}
	/*
	private void saveFiles(Bbs bbs, List<MultipartFile> files) {
        for (MultipartFile file : files) {
            FileBbs fileBbs = new FileBbs();
            fileBbs.setFileId(file.getOriginalFilename()); // 파일 이름 저장
            fileBbs.setBbs(bbs); // 게시글과 연관 설정
            fileBbs.setCreated_time(LocalDateTime.now()); // 현재 시간 저장
            fileBbsRepository.save(fileBbs);

            // 실제 파일 저장 로직 추가 (예: 파일 시스템에 저장)
            try {
                // 파일 저장 로직 구현 (예시)
                // String filePath = "path/to/save/" + file.getOriginalFilename();
                // file.transferTo(new File(filePath));
            } catch (IOException e) {
                // 파일 저장 실패 처리
                throw new RuntimeException("Failed to save file", e);
            }
        }
    }
	*/
	public void deleteById(Long id) {
		bbsRepository.deleteById(id);
	}

	public Bbs update(Bbs bbs) {
		return bbsRepository.save(bbs);
	}


	
}

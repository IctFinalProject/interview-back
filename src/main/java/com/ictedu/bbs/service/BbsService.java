package com.ictedu.bbs.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import jakarta.transaction.Transactional;

import com.ictedu.adminpage.model.NoticeModel;
import com.ictedu.bbs.model.entity.Bbs;
import com.ictedu.bbs.model.entity.BbsComment;
import com.ictedu.bbs.repository.BbsCommentRepository;
import com.ictedu.bbs.repository.BbsRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class BbsService {

	@Autowired
    private final BbsRepository bbsRepository;
    private final BbsCommentRepository commentRepository;  // 앱솔: 댓글 리포지토리 주입

    @Autowired
    public BbsService(BbsRepository bbsRepository, BbsCommentRepository commentRepository) {
        this.bbsRepository = bbsRepository;
        this.commentRepository = commentRepository;  // 앱솔: 주입
    }
    
    public List<Bbs> findAllDeletedPosts(){
    	return bbsRepository.findByDeleted(1); //'Deleted'값이 1인 게시글을 조회
    }

    public List<Bbs> findAll() {
        return bbsRepository.findAll();
    }

    // 삭제되지 않은 게시글 조회 메서드
    public List<Bbs> findAllByDeleted(int deleted) {
        return bbsRepository.findByDeleted(deleted);  // 삭제 여부에 따른 게시글 조회
    }

    public Optional<Bbs> findById(Long id) {
        return bbsRepository.findById(id);
    }

    @Transactional
    public Bbs insertBbs(BbsDto bbsDto, List<MultipartFile> files) throws IOException {
        Bbs bbs = inputBbs(bbsDto);
        if (files != null && !files.isEmpty()) {
            Map<String, byte[]> fileMap = new HashMap<>();
            for (MultipartFile file : files) {
                fileMap.put(file.getOriginalFilename(), file.getBytes());
            }
            bbs.setFiles(fileMap);
        }
        bbs.setDeletedReason(0);// 신고가 없으므로 기본값 0 설정
        bbs.setStatus("VISIBLE"); //기본값으로 status를 VISIBLE로 설정
        return bbsRepository.save(bbs);
    }

    public Bbs inputBbs(BbsDto bbsDto) {
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
                .status("VISIBLE") // 기본값으로 VISIBLE 상태 설정
                .userId(bbsDto.getUserId())
                .build();
    }

    public Bbs update(Bbs bbs) {
        return bbsRepository.save(bbs);  // 게시글 업데이트(삭제 상태 포함)
    }

    //게시글 및 댓글 삭제 처리 (신고 시)
    @Transactional
    public void deleteBbs(Long id, boolean isReport) {
        Bbs bbs = bbsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
       
        bbs.setDeleted(1); //게시글 숨김 처리
        
        //신고로 인한 삭제일 경우 삭제 이유를 1로 설정
        if (isReport) {
        	bbs.setDeletedReason(1); //신고로 인한 삭제
        } else {
        	bbs.setDeletedReason(0); //일반 삭제
        }
        bbs.setDeleted_date(LocalDateTime.now());
        
        // 댓글 숨김 처리
        List<BbsComment> comments = commentRepository.findByBbs_BbsId(bbs.getBbsId());
        for (BbsComment comment : comments) {
            comment.setDeleted(1);  // 댓글 숨김 처리
            commentRepository.save(comment);  // 댓글 저장
        }
        bbsRepository.save(bbs);
    }



    // 파일 데이터 제공 메서드 추가
    public byte[] getFile(Long bbsId, String fileName) {
        Optional<Bbs> bbsOptional = bbsRepository.findById(bbsId);
        if (bbsOptional.isPresent()) {
            Bbs bbs = bbsOptional.get();
            Map<String, byte[]> files = bbs.getFiles();
            if (files != null && files.containsKey(fileName)) {
                return files.get(fileName);
            } else {
                return null;
            }
        }
        return null;
    }

    public Map<String, byte[]> getFilesByBbsId(Long bbsId) {
        return bbsRepository.findById(bbsId)
            .map(Bbs::getFiles)
            .orElse(new HashMap<>());
    }
    public void incrementHitcount(Long id) {
        Bbs bbs = bbsRepository.findById(id).orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        bbs.incrementHitcount();
        bbsRepository.save(bbs);  // 변경된 조회수를 저장
    }

	
    public Bbs createdBbs(Bbs bbs) {
		//생성 시간 설정
		if (bbs.getCreatedAt() == null) {
			bbs.setCreatedAt(null);
		}
		return bbsRepository.save(bbs);
	}
    
    public List<Bbs> findByUserId(Long id){
    	return bbsRepository.findByUserId_Id(id);
    }
    
    
	
	
	
}

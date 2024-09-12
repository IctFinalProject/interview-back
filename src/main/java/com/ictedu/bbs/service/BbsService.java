package com.ictedu.bbs.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import jakarta.transaction.Transactional;
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
        bbs.setDeletedReason(0);  // 앱솔: 기본값 0 설정
        return bbsRepository.save(bbs);
    }

    public Bbs inputBbs(BbsDto bbsDto) {
        return Bbs.builder()
                .title(bbsDto.getTitle())
                .content(bbsDto.getContent())
                .hitCount(0L)
                .likes(0L)
                .active(1)
                .inactive(0)
                .reported(0)
                .deleted(0)
                .edited(0)
                .type("normal")
                .userId(bbsDto.getUserId())
                .build();
    }

    public Bbs update(Bbs bbs) {
        return bbsRepository.save(bbs);  // 게시글 업데이트(삭제 상태 포함)
    }

    @Transactional
    public void deleteBbs(Long id, boolean isReport) {
        Bbs bbs = bbsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        System.out.println("테스트1");
        // 게시글 숨김 처리 로그 추가
        System.out.println("게시글 삭제 처리: " + bbs.getBbsId());
        System.out.println("테스트2");

        bbs.setDeleted(1);  // 게시글 숨김 처리
        System.out.println("테스트3");

        if (isReport) {
        	   System.out.println("테스트4");
            bbs.setDeletedReason(1);  // 신고로 인한 삭제
            System.out.println("테스트5");
        } else {
        	   System.out.println("테스트6");
            bbs.setDeletedReason(0);  // 일반 삭제
            System.out.println("테스트7");
        }
        System.out.println("테스트8");

        bbs.setDeleted_date(LocalDateTime.now());
        System.out.println("테스트9");

        // 댓글 조회 로그 추가
        List<BbsComment> comments = commentRepository.findByBbs_BbsId(bbs.getBbsId());
        System.out.println("테스트10");
        System.out.println("댓글 조회 완료: 댓글 개수 = " + comments.size());
        System.out.println("테스트11");

        // 댓글 숨김 처리
        for (BbsComment comment : comments) {
        	   System.out.println("테스트12");
            System.out.println("댓글 숨김 처리 시작: " + comment.getCommentId());
            System.out.println("테스트13");
            comment.setDeleted(1);  // 댓글 숨김 처리
            System.out.println("테스트14");
            commentRepository.save(comment);  // 댓글 저장
            System.out.println("테스트15");
            System.out.println("댓글 숨김 처리 완료: " + comment.getCommentId());
            System.out.println("테스트16");
        }

        bbsRepository.save(bbs);
        System.out.println("테스트17");
        System.out.println("게시글과 댓글 모두 숨김 처리 완료");
        System.out.println("테스트18");
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
                System.out.println("File not found in the map for fileName: " + fileName);
                return null;
            }
        }
        System.out.println("Bbs not found for ID: " + bbsId);
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

	
	//좋아요 증가
	public void incrementLikes(Long bbsId) {
		  // BbsRepository 또는 LikeRepository에서 좋아요 추가 로직 구현
	    Bbs bbs = bbsRepository.findById(bbsId).orElseThrow();
	    bbs.setLikes(bbs.getLikes() + 1);
	    bbsRepository.save(bbs);

	   
		
	}
	//좋아요 감소
	public void decrementLikes(Long bbsId) {
		// BbsRepository 또는 LikeRepository에서 좋아요 취소 로직 구현
	    Bbs bbs = bbsRepository.findById(bbsId).orElseThrow();
	    bbs.setLikes(Math.max(bbs.getLikes() - 1, 0));
	    bbsRepository.save(bbs);

	    
		
	}
	
	
}

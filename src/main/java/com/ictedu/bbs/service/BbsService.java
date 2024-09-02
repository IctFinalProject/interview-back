package com.ictedu.bbs.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import jakarta.transaction.Transactional;
import com.ictedu.bbs.model.entity.Bbs;
import com.ictedu.bbs.repository.BbsRepository;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BbsService {

    private final BbsRepository bbsRepository;

    @Autowired
    public BbsService(BbsRepository bbsRepository) {
        this.bbsRepository = bbsRepository;
    }

    public List<Bbs> findAll() {
        return bbsRepository.findAll();
    }

    public Optional<Bbs> findById(Long id) {
        return bbsRepository.findById(id);
    }

    @Transactional
    public Bbs insertBbs(BbsDto bbsDto, List<MultipartFile> files) throws IOException {
        Bbs bbs = inputBbs(bbsDto);
        if (files != null && !files.isEmpty()) {
            List<byte[]> fileList = new ArrayList<>();
            for (MultipartFile file : files) {
                fileList.add(file.getBytes());
            }
            bbs.setFiles(fileList);
        }
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
                .userId(bbsDto.getUserId())
                .build();
    }

    public void deleteById(Long id) {
        bbsRepository.deleteById(id);
    }

    public Bbs update(Bbs bbs) {
        return bbsRepository.save(bbs);
    }
    
    // 파일 데이터 제공 메서드 추가
    public byte[] getFile(Long bbsId, int fileIndex) {
        System.out.println("Fetching file for Bbs ID: " + bbsId + " at index: " + fileIndex);  // 콘솔 로그 추가
        return bbsRepository.findById(bbsId)
                .map(bbs -> {
                    List<byte[]> files = bbs.getFiles();
                    if (files != null && fileIndex < files.size()) {
                        return files.get(fileIndex);
                    } else {
                        System.out.println("File index out of bounds or files list is null");  // 콘솔 로그 추가
                        return null;
                    }
                })
                .orElse(null);
    }
    
}

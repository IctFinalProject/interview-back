/*
package com.ictedu.spring.service.bbs;

import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ictedu.bbs.model.entity.BbsRepository;
import com.ictedu.bbs.model.entity.FileBbs;
import com.ictedu.bbs.model.entity.FileBbsRepository;
import com.ictedu.bbs.model.entity.QnA;
import com.ictedu.bbs.model.entity.QnaRepository;

import jakarta.transaction.Transactional;

import com.ictedu.bbs.model.entity.Bbs;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class QnaService {

    public final QnaRepository qnaRepository;
    
    
    @Autowired
    public QnaService(QnaRepository qnaRepository) {
    	this.qnaRepository =qnaRepository;
    	
    }

    
    public List<QnA> findAll() {
        return qnaRepository.findAll();
    }

    public Optional<QnA> findById(Long id) {
        return qnaRepository.findById(id);
    }
    
    @Transactional
    public QnA insertQna(QnaDto qna) {
    	
    	return qnaRepository.save(inputQna(qna));
    }
    
    public QnA inputQna(QnaDto qna) {
    	//return bbsRepository.save(bbs);
    	return QnA.builder()
    			.question(qna.getQuestion())
    			.answer(qna.getAnswer())
    			.hitCount(0L)
    			.active(1)
    			.inactive(0)
    			.reported(0)
    			.deleted(0)
    			.edited(0)
    			.type("normal")
    			.build();
    }
 
    public void deleteById(Long id) {
    	qnaRepository.deleteById(id);
    }

    public QnA update(QnA qna) {
        return qnaRepository.save(qna);
    }
    
}
*/

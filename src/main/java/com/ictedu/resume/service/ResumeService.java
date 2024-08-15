package com.ictedu.resume.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ictedu.resume.entity.ResumeEntity;
import com.ictedu.resume.repository.ResumeRepository;

@Service
public class ResumeService {
	

	@Autowired
    private ResumeRepository resumeRepository;

    public ResumeService(ResumeRepository resumeRepository) {
        this.resumeRepository = resumeRepository;
    }

    public ResumeEntity saveResume(ResumeEntity resume) {
        return resumeRepository.save(resume);
    }

    public ResumeEntity getResume(Long id) {
        return resumeRepository.findById(id).orElse(null);
    }
}






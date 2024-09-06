package com.ictedu.interview.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ictedu.interview.model.entity.Question;
import com.ictedu.interview.repository.QuestionRepository;

@Service
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    public List<Question> getQuestionsByIds(List<Long> questionIds) {
        return questionRepository.findByIdIn(questionIds);
    }
}

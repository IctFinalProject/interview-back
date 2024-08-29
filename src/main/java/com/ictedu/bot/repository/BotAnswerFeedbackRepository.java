package com.ictedu.bot.repository;

import com.ictedu.bot.entity.BotAnswer;
import com.ictedu.bot.entity.BotAnswerFeedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BotAnswerFeedbackRepository extends JpaRepository<BotAnswerFeedback, Long> {
    Optional<BotAnswerFeedback> findByAnswer(BotAnswer answer);
    List<BotAnswerFeedback> findByAnswerBotId(Long botId);
}
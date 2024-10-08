package com.ictedu.bot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ictedu.bot.entity.BotQuestion;

@Repository
public interface BotQuestionRepository extends JpaRepository<BotQuestion, Long> {

	List<BotQuestion> findByBotId(Long botId);}

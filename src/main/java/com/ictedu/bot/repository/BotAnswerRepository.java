package com.ictedu.bot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ictedu.bot.entity.Bot;
import com.ictedu.bot.entity.BotAnswer;

public interface BotAnswerRepository extends JpaRepository<BotAnswer, Long> {
	List<BotAnswer> findTop10ByBotOrderByCreatedTimeDesc(Bot bot);

	List<BotAnswer> findByBotId(Long botId);
}
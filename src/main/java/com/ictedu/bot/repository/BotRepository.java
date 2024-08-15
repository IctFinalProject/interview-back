package com.ictedu.bot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ictedu.bot.entity.Bot;

public interface BotRepository extends JpaRepository<Bot, Long> {}

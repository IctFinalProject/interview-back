package com.ictedu.bot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ictedu.bot.entity.BotFile;


public interface BotFileRepository extends JpaRepository<BotFile, Long> {}

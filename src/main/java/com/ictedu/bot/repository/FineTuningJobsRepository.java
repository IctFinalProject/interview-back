package com.ictedu.bot.repository;

import com.ictedu.bot.entity.FineTuningJobs;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FineTuningJobsRepository extends JpaRepository<FineTuningJobs, String> {
	 Optional<FineTuningJobs> findByJobId(String jobId);
	  List<FineTuningJobs> findByStatus(String status);
}
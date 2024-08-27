package com.ictedu.adminpage.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ictedu.adminpage.model.QnaModel;
import com.ictedu.user.model.entity.User;

public interface QnaRepository extends JpaRepository<QnaModel, Long> {

}

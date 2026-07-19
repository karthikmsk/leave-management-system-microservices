package com.leave_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.leave_service.model.Leave;

@Repository
public interface LeaveRepository extends JpaRepository<Leave, Long> {

}


package com.leave_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.leave_service.model.LeaveType;

public interface LeaveTypeRepository extends JpaRepository<LeaveType,Long>{

    List<LeaveType> findByIsActiveTrue();

    boolean existsByName(String name);

}

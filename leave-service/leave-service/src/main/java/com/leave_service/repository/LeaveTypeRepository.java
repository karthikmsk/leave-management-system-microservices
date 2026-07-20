package com.leave_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.leave_service.model.LeaveType;

@Repository
public interface LeaveTypeRepository extends JpaRepository<LeaveType,Long>{

}

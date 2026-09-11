package com.leave_service.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.leave_service.model.LeaveRequest;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    List<LeaveRequest> findByEmployeeId(Long employeeId);

    LeaveRequest findByEmployeeIdAndId(Long employeeId, Long id);

    List<LeaveRequest> findByEmployeeIdIn(List<Long> employeeIds);

}


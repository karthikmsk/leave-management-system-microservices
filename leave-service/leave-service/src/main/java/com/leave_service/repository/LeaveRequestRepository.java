package com.leave_service.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.leave_service.model.LeaveRequest;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    List<LeaveRequest> findByEmployeeId(Long employeeId);

    LeaveRequest findByEmployeeIdAndLeaveRequestId(Long employeeId, Long leaveRequestId);

}


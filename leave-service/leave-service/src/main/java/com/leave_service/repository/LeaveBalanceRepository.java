package com.leave_service.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.leave_service.model.LeaveBalance;

public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance,Long>{

    Optional<LeaveBalance> findByEmployeeIdAndLeaveTypeId(Long employeeId,Long leaveTypeId);

    List<LeaveBalance> findByEmployeeId(Long employeeId);

    boolean existsByEmployeeId(Long employeeId);

    Float findRemaingDaysByEmployeeId(Long employeeId);

    Float findUsedDaysByEmployeeId(Long employeeId);

}

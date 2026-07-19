package com.leave_service.service;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.leave_service.client.UserClient;
import com.leave_service.exception.UserNotFoundException;
import com.leave_service.exception.ExternalServiceException;
import com.leave_service.exception.LeaveNotFoundException;
import com.leave_service.model.Leave;
import com.leave_service.model.LeaveStatus;
import com.leave_service.model.LeaveType;
import com.leave_service.repository.LeaveRepository;

// LeaveService.java
@Service
public class LeaveService {
    private final UserClient userClient;
    private final LeaveRepository leaveRepository;

    public LeaveService(UserClient userClient, LeaveRepository leaveRepository) {
        this.userClient = userClient;
        this.leaveRepository = leaveRepository;
    }



    public Leave applyLeave(Long userId, LocalDate startDate, int days, LeaveType leaveType) {
        Map<String, Object> userDetails;
        try {
            userDetails = userClient.getUserById(userId);
            if (userDetails == null || userDetails.isEmpty()) {
                throw new UserNotFoundException("User not found with ID: " + userId);
            }
        } catch (UserNotFoundException e) {
            throw e; // Rethrow to be handled by global exception handler
        } catch (Exception e) {
            throw new ExternalServiceException("Error fetching user details for ID: " + userId, e);
        }

        Leave leave = new Leave();
        leave.setEmployeeId(userId);
        leave.setStartDate(startDate);
        leave.setEndDate(startDate.plusDays(days));
        leave.setLeaveType(leaveType);
        leave.setLeaveStatus(LeaveStatus.PENDING);
        leave.setDescription("Leave application for " + days + " days.");
        leave.setAppliedDate(LocalDate.now());
        leave.setTotalDays(days);
        leave.setApproverId((Long) userDetails.get("managerId"));

        return leaveRepository.save(leave);
    }

    // public Leave approveLeave(Long id,Long approverId) {
    //     Leave leave = leaveRepository.findById(id)
    //             .orElseThrow(() -> new LeaveNotFoundException("Leave not found with ID: " + id, null));
    //     if (leave.getLeaveStatus() != LeaveStatus.PENDING) {
    //         throw new IllegalStateException("Only pending leaves can be approved.");
    //     }
    //     leave.setLeaveStatus(LeaveStatus.APPROVED);
    //     leave.setApproverId(approverId);
    //     leave.setApprovedDate(LocalDate.now());

    //     return leaveRepository.save(leave);
    // }

    public Leave rejectLeave(Long id) {
        Leave leave = leaveRepository.findById(id)
                .orElseThrow(() -> new LeaveNotFoundException("Leave not found with ID: " + id, null));
        if (leave.getLeaveStatus() == LeaveStatus.APPROVED) {
            throw new IllegalStateException("Cannot reject an approved leave.");
        }
        leave.setLeaveStatus(LeaveStatus.REJECTED);
        return leaveRepository.save(leave);
    }   
    public Leave cancelLeave(Long id) {
        Leave leave = leaveRepository.findById(id)
                .orElseThrow(() -> new LeaveNotFoundException("Leave not found with ID: " + id, null));
        if (leave.getLeaveStatus() == LeaveStatus.APPROVED) {
            throw new IllegalStateException("Cannot cancel an approved leave.");
        }
        leave.setLeaveStatus(LeaveStatus.CANCELLED);
        return leaveRepository.save(leave);
    }   

    public void deleteLeave(Long id) {
        if (!leaveRepository.existsById(id)) {
            throw new LeaveNotFoundException("Leave not found with ID: " + id, null);
        }
        leaveRepository.deleteById(id);
    }
}
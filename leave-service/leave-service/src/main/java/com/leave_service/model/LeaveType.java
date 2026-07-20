package com.leave_service.model;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name="leave_types")
public class LeaveType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Float annualAllocation;

    private Boolean carryForwardAllowed;
    
    private Float maxCarryForwardDays;

    @Column(nullable = true)
    private boolean isActive;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}

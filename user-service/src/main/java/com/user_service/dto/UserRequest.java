package com.user_service.dto;

import java.time.LocalDate;
import com.user_service.model.Department;
import com.user_service.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRequest {

    @NotBlank
    @Size(max = 100)
    private String name;

    @Email(message = "Invalid Email")
    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 8)
    private String password;

    @NotNull
    private Role role;

    @NotNull
    private Long managerId;

    @NotNull
    private String phoneNumber;

    @NotNull
    private Department department;

    private LocalDate joiningDate;

}

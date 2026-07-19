package com.user_service.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.user_service.model.Department;
import com.user_service.model.Role;
import com.user_service.model.User;
import com.user_service.model.UserStatus;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Page<User> findByManagerId(Long managerId, Pageable pageable);

    boolean existsByEmail(String email);

    @Query("Select MAX(e.employeeId) FROM User e")
    Long findHighestEmployeeId();

    Page<User> findByRole(Role role, Pageable pageable);

    Optional<User> findByEmployeeId(Long employeeId);

    Optional<User> findByEmail(String email);

    Page<User> findByNameContainingIgnoreCase(String name,Pageable pageable);

   Page<User> findByDepartment(Department department,Pageable pageable);

   Page<User> findByStatus(UserStatus status, Pageable pageable);

}

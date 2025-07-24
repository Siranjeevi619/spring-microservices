package com.cityzen.UserManagementService.repository;

import com.cityzen.UserManagementService.models.ERole;
import com.cityzen.UserManagementService.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepo extends JpaRepository<Role,Long> {
    Optional<Role> findByName(ERole name);
}

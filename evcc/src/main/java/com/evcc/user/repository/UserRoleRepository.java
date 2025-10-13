package com.evcc.user.repository;

import com.evcc.user.entity.UserRole;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;



public interface UserRoleRepository extends JpaRepository<UserRole, UUID> {
}

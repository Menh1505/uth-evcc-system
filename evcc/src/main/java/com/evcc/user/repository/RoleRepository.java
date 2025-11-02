package com.evcc.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.evcc.user.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Find role by name
     */
    Optional<Role> findByName(String name);

    /**
     * Check if role name exists
     */
    boolean existsByName(String name);
}
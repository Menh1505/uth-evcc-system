package com.evcc.group.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.evcc.group.entity.Group;

/**
 * Repository interface cho Group entity
 */
@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    
    /**
     * Tìm nhóm theo tên (case-insensitive)
     */
    boolean existsByNameIgnoreCase(String name);
}
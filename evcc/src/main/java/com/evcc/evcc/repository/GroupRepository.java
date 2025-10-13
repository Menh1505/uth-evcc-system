package com.evcc.evcc.repository;

import com.evcc.evcc.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GroupRepository extends JpaRepository<Group, UUID> {

    /**
     * Find group by name
     */
    Optional<Group> findByName(String name);

    /**
     * Find groups by name containing (case-insensitive)
     */
    List<Group> findByNameContainingIgnoreCase(String name);

    /**
     * Find groups by contract ID
     */
    List<Group> findByContractId(UUID contractId);

    /**
     * Check if group name exists
     */
    boolean existsByName(String name);

    /**
     * Find groups without contract
     */
    List<Group> findByContractIdIsNull();

    /**
     * Find groups with contract
     */
    List<Group> findByContractIdIsNotNull();

    /**
     * Custom query to find groups by partial name
     */
    @Query("SELECT g FROM Group g WHERE g.name LIKE %:name%")
    List<Group> findByNameContaining(@Param("name") String name);

    /**
     * Find groups created today
     */
    @Query(value = "SELECT * FROM groups WHERE created_at >= CURRENT_DATE", nativeQuery = true)
    List<Group> findGroupsCreatedToday();

    /**
     * Count groups by contract ID
     */
    long countByContractId(UUID contractId);

    /**
     * Find groups with active memberships
     */
    @Query("SELECT DISTINCT g FROM Group g JOIN g.memberships m WHERE m.status = 'ACTIVE'")
    List<Group> findGroupsWithActiveMembers();
}

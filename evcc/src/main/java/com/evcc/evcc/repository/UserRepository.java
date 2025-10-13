package com.evcc.evcc.repository;

import com.evcc.evcc.entity.User;
import com.evcc.evcc.entity.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Find user by email
     */
    Optional<User> findByEmail(String email);

    /**
     * Find user by phone
     */
    Optional<User> findByPhone(String phone);

    /**
     * Find users by status
     */
    List<User> findByStatus(UserStatus status);

    /**
     * Find users by email containing (case-insensitive)
     */
    List<User> findByEmailContainingIgnoreCase(String email);

    /**
     * Check if email exists
     */
    boolean existsByEmail(String email);

    /**
     * Check if phone exists
     */
    boolean existsByPhone(String phone);

    /**
     * Custom query to find users by partial email
     */
    @Query("SELECT u FROM User u WHERE u.email LIKE %:email%")
    List<User> findByEmailContaining(@Param("email") String email);

    /**
     * Find active users
     */
    @Query("SELECT u FROM User u WHERE u.status = 'ACTIVE'")
    List<User> findActiveUsers();

    /**
     * Native SQL query example
     */
    @Query(value = "SELECT * FROM users WHERE created_at >= CURRENT_DATE", nativeQuery = true)
    List<User> findUsersCreatedToday();

    /**
     * Count users by status
     */
    long countByStatus(UserStatus status);
}

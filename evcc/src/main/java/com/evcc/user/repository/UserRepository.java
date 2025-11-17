package com.evcc.user.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.evcc.user.entity.User;




@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Find user by username
     */
    Optional<User> findByUsername(String username);

    /**
     * Find user by username with roles eagerly fetched
     * Giải quyết LazyInitializationException khi access roles
     */
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.username = :username")
    Optional<User> findByUsernameWithRoles(@Param("username") String username);

    /**
     * Find users by username containing (case-insensitive)
     */
    List<User> findByUsernameContainingIgnoreCase(String username);

    /**
     * Check if username exists
     */
    boolean existsByUsername(String username);

    /**
     * Custom query to find users by partial username
     */
    @Query("SELECT u FROM User u WHERE u.username LIKE %:username%")
    List<User> findByUsernameContaining(@Param("username") String username);

    /**
     * Native SQL query example
     */
    @Query(value = "SELECT * FROM users WHERE created_at >= CURRENT_DATE", nativeQuery = true)
    List<User> findUsersCreatedToday();

    /**
     * Find users that are not verified
     */
    List<User> findByIsVerifiedFalse();

    /**
     * Find users that are verified
     */
    List<User> findByIsVerifiedTrue();

    /**
     * Count unverified users
     */
    long countByIsVerifiedFalse();

    /**
     * Count verified users
     */
    long countByIsVerifiedTrue();
}

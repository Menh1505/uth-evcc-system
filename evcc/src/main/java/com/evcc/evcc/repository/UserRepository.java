package com.evcc.evcc.repository;

import com.evcc.evcc.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by username
     */
    Optional<User> findByUsername(String username);

    /**
     * Find user by email
     */
    Optional<User> findByEmail(String email);

    /**
     * Find users by full name containing (case-insensitive)
     */
    List<User> findByFullNameContainingIgnoreCase(String fullName);

    /**
     * Check if username exists
     */
    boolean existsByUsername(String username);

    /**
     * Check if email exists
     */
    boolean existsByEmail(String email);

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
}

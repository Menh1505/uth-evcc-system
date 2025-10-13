package com.evcc.user.repository;

import com.evcc.user.entity.User;
import com.evcc.user.entity.UserStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;




@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Find user by username
     */
    Optional<User> findByUsername(String username);

    /**
     * Find user by phone
     */
    Optional<User> findByPhone(String phone);

    /**
     * Find users by status
     */
    List<User> findByStatus(UserStatus status);

    /**
     * Find users by username containing (case-insensitive)
     */
    List<User> findByUsernameContainingIgnoreCase(String username);

    /**
     * Check if username exists
     */
    boolean existsByUsername(String username);

    /**
     * Check if phone exists
     */
    boolean existsByPhone(String phone);

    /**
     * Custom query to find users by partial username
     */
    @Query("SELECT u FROM User u WHERE u.username LIKE %:username%")
    List<User> findByUsernameContaining(@Param("username") String username);

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

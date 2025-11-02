package com.evcc.user.entity;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // JPA/Jakarta mới
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false, unique = true, length = 100)
    private String username;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    /**
     * Số căn cước công dân
     */
    @Column(name = "citizen_id", length = 20)
    private String citizenId;

    /**
     * Số bằng lái xe
     */
    @Column(name = "driver_license", length = 20)
    private String driverLicense;

    /**
     * Trạng thái xác minh tài khoản
     */
    @Column(name = "is_verified", nullable = false)
    private Boolean isVerified = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Mối quan hệ ManyToMany với Role
     * User là owning side của quan hệ
     */
    @ManyToMany
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;

    /**
     * Quan hệ OneToMany với GroupMembership
     * Một User có thể tham gia nhiều nhóm thông qua GroupMembership
     */
    @OneToMany(mappedBy = "user")
    @JsonIgnore // Tránh infinite loop khi serialize JSON
    private Set<com.evcc.group.entity.GroupMembership> memberships;

    // Bắt buộc theo JPA: no-arg constructor public/protected
    protected User() {}

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.isVerified = false; // Mặc định chưa xác minh
    }

    public User(String username, String password, String citizenId, String driverLicense) {
        this.username = username;
        this.password = password;
        this.citizenId = citizenId;
        this.driverLicense = driverLicense;
        this.isVerified = false; // Mặc định chưa xác minh
    }

    @PrePersist
    private void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    private void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public String getCitizenId() {
        return citizenId;
    }

    public void setCitizenId(String citizenId) {
        this.citizenId = citizenId;
    }

    public String getDriverLicense() {
        return driverLicense;
    }

    public void setDriverLicense(String driverLicense) {
        this.driverLicense = driverLicense;
    }

    public Boolean getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(Boolean isVerified) {
        this.isVerified = isVerified;
    }

    public Set<com.evcc.group.entity.GroupMembership> getMemberships() {
        return memberships;
    }

    public void setMemberships(Set<com.evcc.group.entity.GroupMembership> memberships) {
        this.memberships = memberships;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", citizenId='" + citizenId + '\'' +
                ", driverLicense='" + driverLicense + '\'' +
                ", isVerified=" + isVerified +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}

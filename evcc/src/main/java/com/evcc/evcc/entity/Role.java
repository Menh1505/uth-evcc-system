package com.evcc.evcc.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleCode code;

    @Column(nullable = false)
    private String name;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public RoleCode getCode() { return code; }
    public void setCode(RoleCode code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
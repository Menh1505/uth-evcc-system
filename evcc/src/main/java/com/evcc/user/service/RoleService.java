package com.evcc.user.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.evcc.user.entity.Role;
import com.evcc.user.repository.RoleRepository;




@Service
public class RoleService {
    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Role saveRole(Role role) {
        return roleRepository.save(role);
    }
}

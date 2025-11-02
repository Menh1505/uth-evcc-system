package com.evcc.user.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.evcc.user.entity.UserRole;
import com.evcc.user.repository.UserRoleRepository;




@Service
public class UserRoleService {
    private final UserRoleRepository userRoleRepository;

    public UserRoleService(UserRoleRepository userRoleRepository) {
        this.userRoleRepository = userRoleRepository;
    }

    public List<UserRole> getAllUserRoles() {
        return userRoleRepository.findAll();
    }

    public UserRole saveUserRole(UserRole userRole) {
        return userRoleRepository.save(userRole);
    }
}

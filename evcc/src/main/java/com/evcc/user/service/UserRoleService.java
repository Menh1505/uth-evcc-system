package com.evcc.user.service;

import com.evcc.user.entity.UserRole;
import com.evcc.user.repository.UserRoleRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;




@Service
public class UserRoleService {
    @Autowired
    private UserRoleRepository userRoleRepository;

    public List<UserRole> getAllUserRoles() {
        return userRoleRepository.findAll();
    }

    public UserRole saveUserRole(UserRole userRole) {
        return userRoleRepository.save(userRole);
    }
}

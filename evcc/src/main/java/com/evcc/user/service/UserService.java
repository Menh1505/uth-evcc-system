package com.evcc.user.service;

import com.evcc.user.entity.User;
import com.evcc.user.repository.UserRepository;
import java.util.List;
import org.springframework.stereotype.Service;




@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }
}

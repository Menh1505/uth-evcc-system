package com.evcc.user.controller;

import com.evcc.user.entity.User;
import com.evcc.user.service.UserService;
import java.util.List;
import org.springframework.web.bind.annotation.*;




@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping
    public User saveUser(@RequestBody User user) {
        return userService.saveUser(user);
    }
}

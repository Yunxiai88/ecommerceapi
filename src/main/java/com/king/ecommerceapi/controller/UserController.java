package com.king.ecommerceapi.controller;

import java.util.List;

import com.king.ecommerceapi.model.User;
import com.king.ecommerceapi.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @Autowired
    UserRepository userRepository;

    @GetMapping("/users")
    public List<User> getAll() {
        return userRepository.findAll();
    }

}
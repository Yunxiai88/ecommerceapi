package com.king.ecommerceapi.controller;

import java.util.List;
import java.util.stream.Collectors;

import com.king.ecommerceapi.exception.NotFoundException;
import com.king.ecommerceapi.model.User;
import com.king.ecommerceapi.model.UserAssembler;
import com.king.ecommerceapi.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @Autowired
    UserRepository userRepository;

    @Autowired
    UserAssembler userAssembler;

    @GetMapping("/users")
    public CollectionModel<EntityModel<User>> getAll() {
        List<EntityModel<User>> users = userRepository.findAll().stream().map(userAssembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(users, linkTo(methodOn(UserController.class).getAll()).withSelfRel());
    }

    @GetMapping("/users/{uid}")
    public EntityModel<?> getUser(@PathVariable Long uid) {
        User user = userRepository.findById(uid).orElseThrow(() -> new NotFoundException("User Not Found."));
        return userAssembler.toModel(user);
    }

    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody User user) {
        EntityModel<User> newUser = userAssembler.toModel(userRepository.save(user));
        return ResponseEntity.created(newUser.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(newUser);
    }

    @PutMapping("/users/{uid}")
    public ResponseEntity<?> UpdateUser(@PathVariable Long uid, @RequestBody User user) {
        User updatedUser = userRepository.findById(uid).map(existUser -> {
            existUser.setUserName(user.getUserName());
            return existUser;
        }).orElseGet(() -> {
            user.setId(uid);
            return userRepository.save(user);
        });

        EntityModel<User> userEntity = userAssembler.toModel(updatedUser);
        return ResponseEntity.created(userEntity.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(userEntity);
    }

    @DeleteMapping("/users/{uid}")
    public ResponseEntity<?> deleteUser(@PathVariable Long uid) {
        userRepository.deleteById(uid);
        return ResponseEntity.noContent().build();
    }

}
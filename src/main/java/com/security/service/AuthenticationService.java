package com.security.service;

import com.example.User;
import com.example.UserRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import java.util.Optional;

@ApplicationScoped
public class AuthenticationService {

    private final UserRepository userRepository;

    @Inject
    public AuthenticationService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public Optional<User> login(String username, String password){
        Optional<User> user = Optional.ofNullable(userRepository.findUser(username));
        if(user.isEmpty() || user.get().getPassword().equals(password)) return user;
        return Optional.empty();
    }
}

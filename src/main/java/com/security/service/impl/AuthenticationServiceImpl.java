package com.security.service.impl;

import com.security.service.AuthenticationService;
import com.security.service.UserEntity;
import com.security.service.UserRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import java.util.Optional;

@ApplicationScoped
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;

    @Inject
    public AuthenticationServiceImpl(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public Optional<UserEntity> login(String username, String password){
        Optional<UserEntity> user = Optional.ofNullable(userRepository.findByUsername(username));
        if(user.isEmpty() || user.get().getPassword().equals(password)) return user;
        return Optional.empty();
    }
}

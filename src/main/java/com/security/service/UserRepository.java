package com.security.service;

public interface UserRepository {
    UserEntity findByUsername(String username);
}

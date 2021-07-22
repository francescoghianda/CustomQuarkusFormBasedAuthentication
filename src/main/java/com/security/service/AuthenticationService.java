package com.security.service;

import java.util.Optional;

public interface AuthenticationService {
    Optional<UserEntity> login(String username, String password);
}

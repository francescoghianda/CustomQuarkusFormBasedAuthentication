package com.security.encryption;

import java.util.Optional;

public interface Encryptor {
    String encrypt(String plainText);
    Optional<String> decrypt(String cipherText);
}

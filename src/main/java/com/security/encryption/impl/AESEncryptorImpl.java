package com.security.encryption.impl;

import com.security.config.SecurityConfiguration;
import com.security.encryption.Encryptor;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.InternalServerErrorException;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.Optional;

@ApplicationScoped
public class AESEncryptorImpl implements Encryptor {

    private final Cipher encryptCipher;
    private final Cipher decryptCipher;

    @Inject
    public AESEncryptorImpl(SecurityConfiguration config) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidParameterSpecException, InvalidKeyException, InvalidAlgorithmParameterException {

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[128];
        random.nextBytes(salt);

        KeySpec spec = new PBEKeySpec(config.getEncryptionKey().toCharArray(), salt, 65536, 256);
        SecretKey tmp = factory.generateSecret(spec);
        SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");

        this.encryptCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        this.encryptCipher.init(Cipher.ENCRYPT_MODE, secret);
        AlgorithmParameters params = this.encryptCipher.getParameters();
        byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();

        this.decryptCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        this.decryptCipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv));
    }

    public String encrypt(String plainText) {
        try{
            byte[] cipherText = this.encryptCipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(cipherText);
        }
        catch (IllegalBlockSizeException | BadPaddingException e){
            e.printStackTrace();
            throw new InternalServerErrorException();
        }

    }

    public Optional<String> decrypt(String cipherText){
        try{
            return Optional.of(new String(this.decryptCipher.doFinal(Base64.getDecoder().decode(cipherText)), StandardCharsets.UTF_8));
        }
        catch (IllegalBlockSizeException | BadPaddingException e){
            return Optional.empty();
        }
    }
}

package com.security.cookie;

import com.security.encryption.Encryptor;
import com.security.config.SecurityConfiguration;
import com.security.exception.MalformedCookieException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Random;

@ApplicationScoped
public class SessionCookieProvider {

    private final SecurityConfiguration config;
    private final Encryptor encryptor;
    private final Random random;

    @Inject
    public SessionCookieProvider(Encryptor encryptor, SecurityConfiguration config){
        this.encryptor = encryptor;
        this.config = config;
        this.random = new Random();
    }

    public String getCookie(String principal, String role){
        return config.getSessionCookieName()+"="+generateToken(principal, role)+"; Path=/; HttpOnly; SameSite=Strict";
    }

    public String getLogoutCookie(){
        return config.getSessionCookieName()+"=; Path=/; HttpOnly; SameSite=Strict; Expires=Sun, 27 Oct 1996 02:00:00 GMT";
    }

    public SessionCookie decodeCookie(String encoded) throws MalformedCookieException {
        String[] decoded = encryptor.decrypt(encoded).orElseThrow(MalformedCookieException::new).split(";");

        if(decoded.length != 5) throw new MalformedCookieException();

        try{
            String principal = decoded[1];
            String role = decoded[2];
            Instant expTime = Instant.parse(decoded[3]);
            Instant issueTime = Instant.parse(decoded[4]);

            return new SessionCookie(principal, role, expTime, issueTime);
        }
        catch (DateTimeParseException e){
            throw new MalformedCookieException();
        }
    }

    private String generateToken(String principal, String role){
        Instant issued = Instant.now();
        String expTime = issued.plusSeconds(config.getSessionCookieTimeoutInterval().toSeconds()).toString();

        byte[] randomData = new byte[16];
        random.nextBytes(randomData);
        String randomString = new String(randomData).replaceAll(";", ".");

        String token = String.format("%s;%s;%s;%s;%s", randomString, principal, role, expTime, issued);
        return encryptor.encrypt(token);
    }
}

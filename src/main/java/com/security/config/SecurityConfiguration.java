package com.security.config;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.InternalServerErrorException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.util.Base64;
import java.util.Optional;
import java.util.Random;

@ApplicationScoped
public class SecurityConfiguration {

    @ConfigProperty(name = "hsc.securyty.session.cookie.name", defaultValue = "session")
    String sessionCookieName;

    @ConfigProperty(name = "hsc.security.session.encryption-key")
    Optional<String> encryptionKey;

    private String digestedEncryptionKey;

    @ConfigProperty(name = "hsc.security.session.cookie.new-cookie-interval", defaultValue = "PT1M")
    String sessionCookieRefreshInterval;

    @ConfigProperty(name = "hsc.security.session.cookie.timeout", defaultValue = "PT30M")
    String sessionCookieTimeout;

    @ConfigProperty(name = "hsc.security.login-page", defaultValue = "/login.html")
    String loginPagePath;

    @ConfigProperty(name = "hsc.security.location-cookie-name", defaultValue = "redirect")
    String locationCookieName;

    @ConfigProperty(name = "hsc.security.redirect-after-login", defaultValue = "true")
    boolean redirectAfterLogin;

    public String getSessionCookieName(){
        return this.sessionCookieName;
    }

    public String getEncryptionKey(){
        if(digestedEncryptionKey == null){
            try{
                MessageDigest digest = MessageDigest.getInstance("SHA-256");

                if(encryptionKey.isEmpty()){
                    Random random = new Random();
                    byte[] randomKey = new byte[32];
                    random.nextBytes(randomKey);
                    encryptionKey = Optional.of(new String(Base64.getEncoder().encode(randomKey)));
                }

                digestedEncryptionKey = Base64.getEncoder().encodeToString(digest.digest(encryptionKey.get().getBytes(StandardCharsets.UTF_8)));
            }
            catch (NoSuchAlgorithmException e){
                throw new InternalServerErrorException();
            }
        }

        return digestedEncryptionKey;
    }

    public Duration getSessionCookieRefreshInterval(){
        try{
            return Duration.parse(sessionCookieRefreshInterval);
        }
        catch (DateTimeParseException e){
            return Duration.ofSeconds(60);
        }
    }

    public Duration getSessionCookieTimeoutInterval(){
        try{
            return Duration.parse(sessionCookieTimeout);
        }
        catch (DateTimeParseException e){
            return Duration.ofSeconds(1800);
        }
    }

    public String getLoginPagePath() {
        return loginPagePath;
    }

    public String getLocationCookieName() {
        return locationCookieName;
    }

    public boolean redirectAfterLogin() {
        return redirectAfterLogin;
    }
}

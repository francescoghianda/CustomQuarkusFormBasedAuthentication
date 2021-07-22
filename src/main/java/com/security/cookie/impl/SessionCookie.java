package com.security.cookie.impl;

import java.time.Instant;

public class SessionCookie {

    private final String principal;
    private final String role;
    private final Instant expireTime;
    private final Instant issueTime;

    public SessionCookie(String principal, String role, Instant expireTime, Instant issueTime){
        this.principal = principal;
        this.role = role;
        this.expireTime = expireTime;
        this.issueTime = issueTime;
    }

    public String getPrincipal() {
        return principal;
    }

    public String getRole() {
        return role;
    }

    public boolean isExpired(){
        return Instant.now().isAfter(expireTime);
    }

    public long secondsFromIssue(){
        return Instant.now().minusSeconds(issueTime.getEpochSecond()).getEpochSecond();
    }
}

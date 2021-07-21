package com.security.utils;

public class AuthenticationIdentity {

    private final String principal;
    private final String role;
    private final boolean authenticated;

    private AuthenticationIdentity(String principal, String role, boolean authenticated){
        this.principal = principal;
        this.role = role;
        this.authenticated = authenticated;
    }

    public String getPrincipal() {
        return principal;
    }

    public String getRole() {
        return role;
    }

    public boolean isAuthenticated(){
        return this.authenticated;
    }

    public static AuthenticationIdentity notAuthenticated(){
        return new AuthenticationIdentity(null, null, false);
    }

    public static AuthenticationIdentity authenticated(String principal, String role){
        return new AuthenticationIdentity(principal, role, true);
    }

}

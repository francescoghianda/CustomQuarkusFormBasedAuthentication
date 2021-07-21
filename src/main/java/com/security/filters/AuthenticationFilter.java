package com.security.filters;

import com.security.utils.AuthenticationIdentity;
import com.security.exception.MalformedCookieException;
import com.security.cookie.SessionCookie;
import com.security.cookie.SessionCookieProvider;
import com.security.config.SecurityConfiguration;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.*;
import javax.ws.rs.core.*;
import java.security.Principal;

@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private final SecurityConfiguration config;
    private final SessionCookieProvider cookieProvider;

    public AuthenticationFilter(SessionCookieProvider cookieProvider, SecurityConfiguration config){
        this.cookieProvider = cookieProvider;
        this.config = config;
    }

    @Override
    public void filter(ContainerRequestContext requestContext) {

        AuthenticationIdentity authenticationIdentity = checkAuthentication(requestContext);

        boolean authenticated = authenticationIdentity.isAuthenticated();
        String principal = authenticationIdentity.getPrincipal();
        String role = authenticationIdentity.getRole();

        requestContext.setSecurityContext(new SecurityContext() {
            @Override
            public Principal getUserPrincipal() {
                return () -> authenticated ? principal : null;
            }

            @Override
            public boolean isUserInRole(String s) {
                return authenticated && role.equals(s);
            }

            @Override
            public boolean isSecure() {
                return false;
            }

            @Override
            public String getAuthenticationScheme() {
                return "basic";
            }
        });
    }

    private AuthenticationIdentity checkAuthentication(ContainerRequestContext context){
        if(!context.getCookies().containsKey(config.getSessionCookieName())) return AuthenticationIdentity.notAuthenticated();

        try {
            SessionCookie sessionCookie = cookieProvider.decodeCookie(context.getCookies().get(config.getSessionCookieName()).getValue());

            if(sessionCookie.isExpired()) return AuthenticationIdentity.notAuthenticated(); // The token is expired

            return AuthenticationIdentity.authenticated(sessionCookie.getPrincipal(), sessionCookie.getRole());
        }
        catch (MalformedCookieException e){
            return AuthenticationIdentity.notAuthenticated();
        }
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {

        if(requestContext.getCookies().containsKey(config.getSessionCookieName()) && requestContext.getSecurityContext().getUserPrincipal().getName() != null){

            try {
                SessionCookie sessionCookie = cookieProvider.decodeCookie(requestContext.getCookies().get(config.getSessionCookieName()).getValue());

                if(sessionCookie.secondsFromIssue()  > config.getSessionCookieRefreshInterval().toSeconds()){
                    responseContext.getHeaders().add("Set-Cookie", cookieProvider.getCookie(sessionCookie.getPrincipal(), sessionCookie.getRole()));
                }
            }
            catch (MalformedCookieException e){
                e.printStackTrace();
            }
        }
    }
}

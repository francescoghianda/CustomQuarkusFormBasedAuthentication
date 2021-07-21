package com.security.filters;

import com.security.config.SecurityConfiguration;
import io.quarkus.security.Authenticated;
import io.vertx.core.http.HttpMethod;

import javax.annotation.Priority;
import javax.annotation.security.DenyAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Arrays;

@Priority(Priorities.AUTHORIZATION)
public class AuthorizationFilter implements ContainerRequestFilter {

    @Context
    private ResourceInfo resourceInfo;

    private final SecurityConfiguration config;

    public AuthorizationFilter(SecurityConfiguration config){
        this.config = config;
    }

    @Override
    public void filter(ContainerRequestContext requestContext) {

        //RolesAllowed annotation on class
        RolesAllowed rolesAllowed = resourceInfo.getResourceClass().getAnnotation(RolesAllowed.class);
        if (rolesAllowed != null && !hasRole(rolesAllowed.value(), requestContext.getSecurityContext())) {
            abort(requestContext);
            return;
        }

        /*if (resourceInfo.getResourceClass().isAnnotationPresent(PermitAll.class)) {
            return;
        }*/

        //Annotations on method
        Method method = resourceInfo.getResourceMethod();

        if (method.isAnnotationPresent(DenyAll.class)) {
            abort(requestContext);
            return;
        }

        if (method.isAnnotationPresent(Authenticated.class) && requestContext.getSecurityContext().getUserPrincipal() == null) {
            abort(requestContext);
            return;
        }

        rolesAllowed = method.getAnnotation(RolesAllowed.class);
        if (rolesAllowed != null && !hasRole(rolesAllowed.value(), requestContext.getSecurityContext())) {
            abort(requestContext);
        }
    }

    private void abort(ContainerRequestContext requestContext){
        Response.ResponseBuilder response;

        if(requestContext.getMethod().equals(HttpMethod.GET.name())){
            response = Response.temporaryRedirect(URI.create(config.getLoginPagePath()));
            if(config.redirectAfterLogin()){
                String redirectCookie = config.getLocationCookieName()+"="+requestContext.getUriInfo().getRequestUri().getPath()+"; Path=/; HttpOnly";
                response = response.header("Set-Cookie", redirectCookie);
            }
        }
        else{
            response = Response.status(Response.Status.FORBIDDEN);
        }

        requestContext.abortWith(response.build());
    }

    private boolean hasRole(String[] roles, SecurityContext securityContext){
        return securityContext != null && Arrays.stream(roles).anyMatch(securityContext::isUserInRole);
    }
}

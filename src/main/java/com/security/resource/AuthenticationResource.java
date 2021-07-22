package com.security.resource;

import com.security.cookie.SessionCookieProvider;
import com.security.service.AuthenticationService;
import com.security.config.SecurityConfiguration;
import com.security.service.UserEntity;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Optional;

@Path("/")
public class AuthenticationResource {

    private final SecurityConfiguration config;
    private final AuthenticationService authService;
    private final SessionCookieProvider cookieProvider;

    @Inject
    public AuthenticationResource(AuthenticationService authService, SessionCookieProvider cookieProvider, SecurityConfiguration config){
        this.authService = authService;
        this.cookieProvider = cookieProvider;
        this.config = config;
    }

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response login(@FormParam("username") String username, @FormParam("password") String password, @CookieParam("redirect") String redirectPath){
        Optional<UserEntity> user = authService.login(username, password);
        if(user.isEmpty()) return Response.status(Response.Status.FORBIDDEN).build();

        Response.ResponseBuilder response;
        if(redirectPath != null && !redirectPath.isEmpty() && config.redirectAfterLogin())response = Response.seeOther(URI.create(redirectPath)).header("Set-Cookie", "redirect=; HttpOnly; Path=/; SameSite=Strict; expires=Thu, 01 Jan 1970 00:00:00 GMT");
        else response = Response.ok();

        return response.header("Set-Cookie", cookieProvider.getCookie(user.get().getUsername(), user.get().getRole())).build();
    }

    @GET
    @Path("/logout")
    public Response logout(@Context ContainerRequestContext context){
        return Response.seeOther(URI.create(config.getLoginPagePath())).header("Set-Cookie", cookieProvider.getLogoutCookie()).build();
    }
}

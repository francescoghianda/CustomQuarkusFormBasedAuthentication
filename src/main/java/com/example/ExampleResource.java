package com.example;

import com.security.encryption.Encryptor;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/hello")
public class ExampleResource {

    private final UserRepository userRepository;
    private final Encryptor encryptor;

    @Inject
    public ExampleResource(UserRepository userRepository, Encryptor encryptor){
        this.userRepository = userRepository;
        this.encryptor = encryptor;
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Hello RESTEasy";
    }

    @GET
    @Path("/auth")
    @Produces(MediaType.TEXT_PLAIN)
    @RolesAllowed("user")
    public String auth(){
        return "Authenticated!";
    }

    @GET
    @Path("/user/{username}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getUser(@PathParam("username") String username){
        return userRepository.findUser(username).toString();
    }

}
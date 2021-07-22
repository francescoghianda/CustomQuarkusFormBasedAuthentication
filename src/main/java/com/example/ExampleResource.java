package com.example;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/")
public class ExampleResource {

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

}
package com;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/hello-resteasy-reactive")
public class ReactiveGreetingResource {

//    @GET
//    @Produces(MediaType.TEXT_PLAIN)
//    public String hello() {
//        return "Hello RESTEasy Reactive";
//    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Multi<String> helloUni() {
        return Multi.createFrom().items("multi1<br>", "multi2<br>", "multi3");
    }
}
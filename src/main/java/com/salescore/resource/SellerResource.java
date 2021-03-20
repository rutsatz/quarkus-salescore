package com.salescore.resource;

import com.salescore.model.Seller;
import io.smallrye.mutiny.Multi;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/api/v1/seller")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SellerResource {

    @GET
    public Multi<Seller> listAll() {
        return Seller.streamAll();
    }

}
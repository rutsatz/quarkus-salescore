package com.salescore.resource;

import com.salescore.dto.SellerDTO;
import com.salescore.model.Seller;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoEntityBase;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import org.bson.types.ObjectId;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import java.net.URI;

@Path("/api/v1/sellers")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SellerResource {

    @Inject
    Logger log;

    @GET
    @Path("/{id}")
    public Uni<SellerDTO> findById(@PathParam("id") String id) {
        return Seller.findById(new ObjectId(id))
                .onSubscribe().invoke(() -> log.tracef("Searching for seller with id %s", id))
                .onItem().ifNull().failWith(NotFoundException::new)
                .map(s -> convertEntityToDto((Seller) s));
    }

    @GET
    public Multi<SellerDTO> listAll() {
        return Seller.streamAll()
                .onSubscribe().invoke(() -> log.trace("Listing all sellers"))
                .onItem().transform(entity -> convertEntityToDto((Seller) entity));
    }

    // TODO: filter

    @POST
    public Uni<Response> create(SellerDTO dto) {
        var seller = convertDtoToEntity(dto);
        return seller.persist()
                .onSubscribe().invoke(() -> log.debugf("Saving new seller %s", dto))
                .flatMap(u -> Seller.findById(seller.id))
                .map(s -> String.format("/api/v1/sellers/%s", ((Seller) s).id))
                .map(id -> Response.created(URI.create(id)))
                .map(ResponseBuilder::build);
    }

    @PUT
    @Path("/{id}")
    public Uni<SellerDTO> update(SellerDTO dto, @PathParam("id") String id) {
        return Seller.findById(new ObjectId(id))
                .onSubscribe().invoke(() -> log.debugf("Updating seller %s", dto))
                .onItem().ifNull().failWith(NotFoundException::new)
                .map(entity -> ((Seller) entity).toEntity(dto))
                .onItem().call(seller -> seller.update())
                .map(this::convertEntityToDto);
    }

    @DELETE
    @Path("/{id}")
    public Uni<Response> delete(@PathParam("id") String id) {
        return Seller.findById(new ObjectId(id))
                .onSubscribe().invoke(() -> log.debugf("Deleting seller %s", id))
                .onItem().ifNull().failWith(NotFoundException::new)
                .flatMap(ReactivePanacheMongoEntityBase::delete)
                .onItem().invoke(() -> log.infof("Seller %s has been successfully deleted", id))
                .map(u -> Response.noContent().build());
    }

    private SellerDTO convertEntityToDto(Seller seller) {
        var dto = new SellerDTO();
        dto.id = seller.id != null ? seller.id.toString() : null;
        dto.name = seller.name;
        dto.registrationNumber = seller.registrationNumber;
        return dto;
    }

    private Seller convertDtoToEntity(SellerDTO dto) {
        var seller = new Seller();
        return seller.toEntity(dto);
    }

}
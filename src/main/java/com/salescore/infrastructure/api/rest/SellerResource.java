package com.salescore.infrastructure.api.rest;

import com.salescore.infrastructure.api.rest.dto.SellerDTO;
import com.salescore.domain.model.Seller;
import com.salescore.application.SellerService;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import java.net.URI;

@Tag(name = "Seller Resource")
@Path("/api/v1/sellers")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SellerResource {

    @Inject
    Logger log;

    @Inject
    SellerService sellerService;

    @Operation(summary = "Find seller by id")
    @GET
    @Path("/{id}")
    public Uni<SellerDTO> findById(@PathParam("id") String id) {
        return sellerService.findById(id)
                .map(this::convertEntityToDto);
    }

    @Operation(summary = "List all sellers")
    @GET
    public Multi<SellerDTO> listAll() {
        return sellerService.listAll()
                .map(this::convertEntityToDto);
    }

    // TODO: filter

    @Operation(summary = "Save new seller")
    @POST
    public Uni<Response> create(@Valid SellerDTO dto) {
        var seller = convertDtoToEntity(dto);
        return sellerService.create(seller)
                .map(s -> String.format("/api/v1/sellers/%s", s.id))
                .map(id -> Response.created(URI.create(id)))
                .map(ResponseBuilder::build);
    }

    @Operation(summary = "Update seller by id")
    @PUT
    @Path("/{id}")
    public Uni<SellerDTO> update(@Valid SellerDTO dto, @PathParam("id") String id) {
        return sellerService.update(dto, id)
                .map(this::convertEntityToDto);
    }

    @Operation(summary = "Delete seller by id")
    @DELETE
    @Path("/{id}")
    public Uni<Response> delete(@PathParam("id") String id) {
        return sellerService.delete(id)
                .map(u -> Response.noContent().build());
    }

    private SellerDTO convertEntityToDto(Seller seller) {
        return seller.toDto(seller);
    }

    private Seller convertDtoToEntity(SellerDTO dto) {
        var seller = new Seller();
        return seller.toEntity(dto);
    }

}
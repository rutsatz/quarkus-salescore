package com.salescore.infrastructure.api.rest;

import com.salescore.infrastructure.api.rest.dto.SaleCreationDTO;
import com.salescore.infrastructure.api.rest.dto.SaleResponseDTO;
import com.salescore.domain.model.Sale;
import com.salescore.application.SaleService;
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
import java.net.URI;

@Tag(name = "Sales Resource")
@Path("/api/v1/sales")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SaleResource {

    @Inject
    Logger log;

    @Inject
    SaleService saleService;

    @Operation(summary = "Find sale by id")
    @GET
    @Path("/{id}")
    public Uni<SaleResponseDTO> findById(@PathParam("id") String id) {
        return saleService.findById(id)
                .map(this::convertEntityToDto);
    }

    @Operation(summary = "List all products")
    @GET
    public Multi<SaleResponseDTO> listAll() {
        return saleService.listAll()
                .onItem().transform(this::convertEntityToDto);
    }

    // TODO: filter

    @Operation(summary = "Save new sale")
    @POST
    public Uni<Response> create(@Valid SaleCreationDTO dto) {
        var sale = convertDtoToEntity(dto);
        return saleService.create(sale)
                .map(s -> String.format("/api/v1/sales/%s", s.id))
                .map(id -> Response.created(URI.create(id)))
                .map(Response.ResponseBuilder::build);
    }

    @Operation(summary = "Update sale by id")
    @PUT
    @Path("/{id}")
    public Uni<SaleResponseDTO> update(@Valid SaleCreationDTO dto, @PathParam("id") String id) {
        return saleService.update(dto, id)
                .map(this::convertEntityToDto);
    }

    @Operation(summary = "Delete sale by id")
    @DELETE
    @Path("/{id}")
    public Uni<Response> delete(@PathParam("id") String id) {
        return saleService.delete(id)
                .map(u -> Response.noContent().build());
    }

    private SaleResponseDTO convertEntityToDto(Sale sale) {
        return sale.toDto(sale);
    }

    private Sale convertDtoToEntity(SaleCreationDTO dto) {
        var sale = new Sale();
        return sale.toEntity(dto);
    }

}

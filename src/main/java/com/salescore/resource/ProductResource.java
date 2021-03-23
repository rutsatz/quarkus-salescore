package com.salescore.resource;

import com.salescore.dto.ProductDTO;
import com.salescore.model.Product;
import com.salescore.service.ProductService;
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

@Tag(name = "Product Resource")
@Path("/api/v1/products")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProductResource {

    @Inject
    Logger log;

    @Inject
    ProductService productService;

    @Operation(summary = "Find product by id")
    @GET
    @Path("/{id}")
    public Uni<ProductDTO> findById(@PathParam("id") String id) {
        return productService.findById(id)
                .map(this::convertEntityToDto);
    }

    @Operation(summary = "List all products")
    @GET
    public Multi<ProductDTO> listAll() {
        return productService.listAll()
                .map(this::convertEntityToDto);
    }

    // TODO: filter

    @Operation(summary = "Save new product")
    @POST
    public Uni<Response> create(@Valid ProductDTO dto) {
        var product = convertDtoToEntity(dto);
        return productService.create(product)
                .map(p -> String.format("/api/v1/products/%s", p.id))
                .map(id -> Response.created(URI.create(id)))
                .map(ResponseBuilder::build);
    }

    @Operation(summary = "Update product by id")
    @PUT
    @Path("/{id}")
    public Uni<ProductDTO> update(@Valid ProductDTO dto, @PathParam("id") String id) {
        return productService.update(dto, id)
                .map(this::convertEntityToDto);
    }

    @Operation(summary = "Delete product by id")
    @DELETE
    @Path("/{id}")
    public Uni<Response> delete(@PathParam("id") String id) {
        return productService.delete(id)
                .map(u -> Response.noContent().build());
    }

    private ProductDTO convertEntityToDto(Product product) {
        return product.toDto(product);
    }

    private Product convertDtoToEntity(ProductDTO dto) {
        var product = new Product();
        return product.toEntity(dto);
    }

}
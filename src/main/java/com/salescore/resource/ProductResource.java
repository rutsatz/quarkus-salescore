package com.salescore.resource;

import com.salescore.dto.ProductDTO;
import com.salescore.model.Product;
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

@Path("/api/v1/products")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProductResource {

    @Inject
    Logger log;

    @GET
    @Path("/{id}")
    public Uni<ProductDTO> findById(@PathParam("id") String id) {
        return Product.findById(new ObjectId(id))
                .onSubscribe().invoke(() -> log.tracef("Searching for product with id %s", id))
                .onItem().ifNull().failWith(NotFoundException::new)
                .map(s -> convertEntityToDto((Product) s));
    }

    @GET
    public Multi<ProductDTO> listAll() {
        return Product.streamAll()
                .onSubscribe().invoke(() -> log.trace("Listing all products"))
                .onItem().transform(entity -> convertEntityToDto((Product) entity));
    }

    // TODO: filter

    @POST
    public Uni<Response> create(ProductDTO dto) {
        var product = convertDtoToEntity(dto);
        return product.persist()
                .onSubscribe().invoke(() -> log.debugf("Saving new product %s", dto))
                .flatMap(u -> Product.findById(product.id))
                .map(p -> String.format("/api/v1/products/%s", ((Product) p).id))
                .map(id -> Response.created(URI.create(id)))
                .map(ResponseBuilder::build);
    }

    @PUT
    @Path("/{id}")
    public Uni<ProductDTO> update(ProductDTO dto, @PathParam("id") String id) {
        return Product.findById(new ObjectId(id))
                .onSubscribe().invoke(() -> log.debugf("Updating product %s", dto))
                .onItem().ifNull().failWith(NotFoundException::new)
                .map(entity -> ((Product) entity).toEntity(dto))
                .onItem().call(product -> product.update())
                .map(this::convertEntityToDto);
    }

    @DELETE
    @Path("/{id}")
    public Uni<Response> delete(@PathParam("id") String id) {
        return Product.findById(new ObjectId(id))
                .onSubscribe().invoke(() -> log.debugf("Deleting product %s", id))
                .onItem().ifNull().failWith(NotFoundException::new)
                .flatMap(ReactivePanacheMongoEntityBase::delete)
                .onItem().invoke(() -> log.infof("Product %s has been successfully deleted", id))
                .map(u -> Response.noContent().build());
    }

    private ProductDTO convertEntityToDto(Product product) {
        var dto = new ProductDTO();
        dto.id = product.id != null ? product.id.toString() : null;
        dto.name = product.name;
        dto.price = product.price;
        return dto;
    }

    private Product convertDtoToEntity(ProductDTO dto) {
        var product = new Product();
        return product.toEntity(dto);
    }

}
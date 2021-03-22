package com.salescore.resource;

import com.salescore.dto.SaleCreationDTO;
import com.salescore.dto.SaleResponseDTO;
import com.salescore.model.Product;
import com.salescore.model.Sale;
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
import java.math.BigDecimal;
import java.net.URI;
import java.util.Objects;

@Path("/api/v1/sales")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SaleResource {

    @Inject
    Logger log;

    @GET
    @Path("/{id}")
    public Uni<SaleResponseDTO> findById(@PathParam("id") String id) {
        return Sale.findById(new ObjectId(id))
                .onSubscribe().invoke(() -> log.tracef("Searching for sale with id %s", id))
                .onItem().ifNull().failWith(NotFoundException::new)
                .map(s -> convertEntityToDto((Sale) s));
    }

    @GET
    public Multi<SaleResponseDTO> listAll() {
        return Sale.streamAll()
                .onSubscribe().invoke(() -> log.trace("Listing all sales"))
                .onItem().transform(entity -> convertEntityToDto((Sale) entity));
    }

    // TODO: filter

    @POST
    public Uni<Response> create(SaleCreationDTO dto) {
        var sale = convertDtoToEntity(dto);

        var seller = Seller.findById(sale.seller.id)
                .onItem().ifNull().failWith(NotFoundException::new);

        var products = Multi.createFrom().items(sale.products.stream())
                .flatMap(product -> Product.findById(product.id)
                        .onItem().ifNull().failWith(NotFoundException::new).toMulti())
                .map(o -> (Product) o)
                .collect().asList();

        return Uni.combine().all().unis(seller, products).asTuple()
                .onItem().transform(tuple -> {
                    sale.seller = (Seller) tuple.getItem1();
                    sale.products = tuple.getItem2();
                    sale.amount = tuple.getItem2().stream()
                            .map(product -> product.price)
                            .filter(Objects::nonNull)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    return sale;
                })
                .flatMap(s -> sale.persist())
                .onSubscribe().invoke(() -> log.debugf("Saving new sale %s", dto))
                .flatMap(u -> Sale.findById(sale.id))
                .map(s -> String.format("/api/v1/sales/%s", ((Sale) s).id))
                .map(id -> Response.created(URI.create(id)))
                .map(Response.ResponseBuilder::build);
    }

    @PUT
    @Path("/{id}")
    public Uni<SaleResponseDTO> update(SaleCreationDTO dto, @PathParam("id") String id) {
        return Sale.findById(new ObjectId(id))
                .onSubscribe().invoke(() -> log.debugf("Updating sale %s", dto))
                .onItem().ifNull().failWith(NotFoundException::new)
                .map(entity -> ((Sale) entity).toEntity(dto))
                .onItem().call(sale -> sale.update())
                .map(this::convertEntityToDto);
    }

    @DELETE
    @Path("/{id}")
    public Uni<Response> delete(@PathParam("id") String id) {
        return Sale.findById(new ObjectId(id))
                .onSubscribe().invoke(() -> log.debugf("Deleting sale %s", id))
                .onItem().ifNull().failWith(NotFoundException::new)
                .flatMap(ReactivePanacheMongoEntityBase::delete)
                .onItem().invoke(() -> log.infof("Sale %s has been successfully deleted", id))
                .map(u -> Response.noContent().build());
    }

    private SaleResponseDTO convertEntityToDto(Sale sale) {
        var dto = new SaleResponseDTO();
        dto.id = sale.id != null ? sale.id.toString() : null;
        dto.seller = sale.seller;
        dto.amount = sale.amount;
        dto.products = sale.products;
        return dto;
    }

    private Sale convertDtoToEntity(SaleCreationDTO dto) {
        var sale = new Sale();
        return sale.toEntity(dto);
    }

}

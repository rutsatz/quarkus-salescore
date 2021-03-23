package com.salescore.infrastructure.service;

import com.salescore.application.SaleService;
import com.salescore.infrastructure.api.rest.dto.SaleCreationDTO;
import com.salescore.domain.model.Product;
import com.salescore.domain.model.Sale;
import com.salescore.domain.model.Seller;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoEntityBase;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.tuples.Tuple2;
import org.bson.types.ObjectId;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@ApplicationScoped
public class SaleServiceImpl implements SaleService {

    @Inject
    Logger log;

    @Override
    public Uni<Sale> findById(String id) {
        return Sale.<Sale>findById(new ObjectId(id))
                .onSubscribe().invoke(() -> log.tracef("Searching for sale with id %s", id))
                .onItem().ifNull().failWith(NotFoundException::new);
    }

    @Override
    public Multi<Sale> listAll() {
        return Sale.<Sale>streamAll()
                .onSubscribe().invoke(() -> log.trace("Listing all sales"));
    }

    @Override
    public Uni<Sale> create(Sale sale) {
        var seller = Seller.<Seller>findById(sale.seller.id)
                .onItem().ifNull().failWith(NotFoundException::new);

        var products = Multi.createFrom().items(sale.products.stream())
                .flatMap(product -> Product.<Product>findById(product.id)
                        .onItem().ifNull().failWith(NotFoundException::new).toMulti())
                .collect().asList();

        return Uni.combine().all().unis(seller, products).asTuple()
                .onItem().transform(tuple -> createSale(sale, tuple))
                .flatMap(s -> sale.persist())
                .onSubscribe().invoke(() -> log.debugf("Saving new sale %s", sale))
                .flatMap(u -> Sale.findById(sale.id));
    }


    @Override
    public Uni<Sale> update(SaleCreationDTO dto, String id) {
        return Sale.findById(new ObjectId(id))
                .onSubscribe().invoke(() -> log.debugf("Updating sale %s", dto))
                .onItem().ifNull().failWith(NotFoundException::new)
                .map(entity -> ((Sale) entity).toEntity(dto))
                .onItem().call(sale -> sale.update());
    }

    @Override
    public Uni<Void> delete(String id) {
        return Sale.findById(new ObjectId(id))
                .onSubscribe().invoke(() -> log.debugf("Deleting sale %s", id))
                .onItem().ifNull().failWith(NotFoundException::new)
                .flatMap(ReactivePanacheMongoEntityBase::delete)
                .onItem().invoke(() -> log.infof("Sale %s has been successfully deleted", id));
    }

    private Sale createSale(Sale sale, Tuple2<Seller, List<Product>> tuple) {
        sale.seller = tuple.getItem1();
        sale.products = tuple.getItem2();
        sale.amount = sale.products.stream()
                .map(product -> product.price)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return sale;
    }

}

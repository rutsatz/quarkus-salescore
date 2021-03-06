package com.salescore.infrastructure.service;

import com.salescore.application.ProductService;
import com.salescore.infrastructure.api.rest.dto.ProductDTO;
import com.salescore.domain.model.Product;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoEntityBase;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import org.bson.types.ObjectId;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;

@ApplicationScoped
public class ProductServiceImpl implements ProductService {

    @Inject
    Logger log;

    @Override
    public Uni<Product> findById(String id) {
        return Product.<Product>findById(new ObjectId(id))
                .onSubscribe().invoke(() -> log.tracef("Searching for product with id %s", id))
                .onItem().ifNull().failWith(NotFoundException::new);
    }

    @Override
    public Multi<Product> listAll() {
        return Product.<Product>streamAll()
                .onSubscribe().invoke(() -> log.trace("Listing all products"));
    }

    @Override
    public Uni<Product> create(Product product) {
        return product.persist()
                .onSubscribe().invoke(() -> log.debugf("Saving new product %s", product))
                .flatMap(u -> Product.findById(product.id));
    }

    @Override
    public Uni<Product> update(ProductDTO dto, String id) {
        return Product.findById(new ObjectId(id))
                .onSubscribe().invoke(() -> log.debugf("Updating product %s", dto))
                .onItem().ifNull().failWith(NotFoundException::new)
                .map(entity -> ((Product) entity).toEntity(dto))
                .onItem().call(product -> product.update());
    }

    @Override
    public Uni<Void> delete(String id) {
        return Product.findById(new ObjectId(id))
                .onSubscribe().invoke(() -> log.debugf("Deleting product %s", id))
                .onItem().ifNull().failWith(NotFoundException::new)
                .flatMap(ReactivePanacheMongoEntityBase::delete)
                .onItem().invoke(() -> log.infof("Product %s has been successfully deleted", id));
    }

}

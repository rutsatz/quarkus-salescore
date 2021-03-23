package com.salescore.infrastructure.service;

import com.salescore.application.SellerService;
import com.salescore.infrastructure.api.rest.dto.SellerDTO;
import com.salescore.domain.model.Seller;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoEntityBase;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import org.bson.types.ObjectId;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;

@ApplicationScoped
public class SellerServiceImpl implements SellerService {

    @Inject
    Logger log;

    @Override
    public Uni<Seller> findById(String id) {
        return Seller.<Seller>findById(new ObjectId(id))
                .onSubscribe().invoke(() -> log.tracef("Searching for seller with id %s", id))
                .onItem().ifNull().failWith(NotFoundException::new);
    }

    @Override
    public Multi<Seller> listAll() {
        return Seller.<Seller>streamAll()
                .onSubscribe().invoke(() -> log.trace("Listing all sellers"));
    }

    @Override
    public Uni<Seller> create(Seller seller) {
        return seller.persist()
                .onSubscribe().invoke(() -> log.debugf("Saving new seller %s", seller))
                .flatMap(u -> Seller.findById(seller.id));
    }

    @Override
    public Uni<Seller> update(SellerDTO dto, String id) {
        return Seller.findById(new ObjectId(id))
                .onSubscribe().invoke(() -> log.debugf("Updating seller %s", dto))
                .onItem().ifNull().failWith(NotFoundException::new)
                .map(entity -> ((Seller) entity).toEntity(dto))
                .onItem().call(seller -> seller.update());
    }

    @Override
    public Uni<Void> delete(String id) {
        return Seller.findById(new ObjectId(id))
                .onSubscribe().invoke(() -> log.debugf("Deleting seller %s", id))
                .onItem().ifNull().failWith(NotFoundException::new)
                .flatMap(ReactivePanacheMongoEntityBase::delete)
                .onItem().invoke(() -> log.infof("Seller %s has been successfully deleted", id));
    }

}

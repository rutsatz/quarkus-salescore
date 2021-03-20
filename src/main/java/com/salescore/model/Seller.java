package com.salescore.model;

import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoEntity;
import io.smallrye.mutiny.Uni;

public class Seller extends ReactivePanacheMongoEntity {

    public String name;
    public Long registrationNumber;

    public static Uni<Seller> findByRegistrationNumber(Long registrationNumber) {
        return find("registrationNumber", registrationNumber)
                .firstResult();
    }
}

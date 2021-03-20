package com.salescore.model;

import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoEntity;

import java.math.BigDecimal;

public class Product extends ReactivePanacheMongoEntity {

    public String name;
    public BigDecimal price;

}

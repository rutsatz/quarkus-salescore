package com.salescore.model;

import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoEntity;

import java.math.BigDecimal;
import java.util.List;

public class Sale extends ReactivePanacheMongoEntity {

    public Seller seller;
    public List<Product> products;
    public BigDecimal amount;

}

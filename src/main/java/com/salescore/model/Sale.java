package com.salescore.model;

import com.salescore.dto.SaleCreationDTO;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoEntity;
import org.bson.types.ObjectId;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class Sale extends ReactivePanacheMongoEntity {

    public Seller seller;
    public List<Product> products;
    public BigDecimal amount;

    /**
     * TODO: Bug relaciado ao gradle. Fix previsto para a próxima release (1.13.x)
     * https://github.com/quarkusio/quarkus/issues/15104
     */
    public Sale toEntity(SaleCreationDTO dto) {
        var seller = new Seller();
        seller.id = new ObjectId(dto.sellerId);

        var products = dto.productsIds.stream()
                .map(id -> {
                    var product = new Product();
                    product.id = new ObjectId(id);
                    return product;
                })
                .collect(Collectors.toList());

        this.seller = seller;
        this.products = products;
        return this;
    }
}

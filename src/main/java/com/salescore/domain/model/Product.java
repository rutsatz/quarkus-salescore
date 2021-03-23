package com.salescore.domain.model;

import com.salescore.infrastructure.api.rest.dto.ProductDTO;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoEntity;

import java.math.BigDecimal;

public class Product extends ReactivePanacheMongoEntity {

    public String name;
    public BigDecimal price;

    /**
     * TODO: Bug relaciado ao gradle. Fix previsto para a próxima release (1.13.x)
     * https://github.com/quarkusio/quarkus/issues/15104
     */
    public Product toEntity(ProductDTO dto) {
        this.name = dto.name;
        this.price = dto.price;
        return this;
    }

    public ProductDTO toDto(Product product) {
        var dto = new ProductDTO();
        dto.id = product.id != null ? product.id.toString() : null;
        dto.name = product.name;
        dto.price = product.price;
        return dto;
    }

    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                ", price=" + price +
                ", id=" + id +
                '}';
    }
}

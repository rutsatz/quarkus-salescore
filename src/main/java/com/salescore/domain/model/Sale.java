package com.salescore.domain.model;

import com.salescore.infrastructure.api.rest.dto.SaleCreationDTO;
import com.salescore.infrastructure.api.rest.dto.SaleResponseDTO;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoEntity;
import org.bson.types.ObjectId;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class Sale extends ReactivePanacheMongoEntity {

    public Seller seller;
    public List<Product> products;
    public BigDecimal amount;

    public Seller getSeller() {
        return seller;
    }

    public void setSeller(Seller seller) {
        this.seller = seller;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

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

    public SaleResponseDTO toDto(Sale sale) {
        var dto = new SaleResponseDTO();
        dto.id = sale.id != null ? sale.id.toString() : null;
        dto.seller = sale.seller;
        dto.amount = sale.amount;
        dto.products = sale.products;
        return dto;
    }

    @Override
    public String toString() {
        return "Sale{" +
                "seller=" + seller +
                ", products=" + products +
                ", amount=" + amount +
                ", id=" + id +
                '}';
    }
}

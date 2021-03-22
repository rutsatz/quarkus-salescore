package com.salescore.dto;

import com.salescore.model.Product;
import com.salescore.model.Seller;

import java.math.BigDecimal;
import java.util.List;

public class SaleResponseDTO {

    public String id;
    public Seller seller;
    public List<Product> products;
    public BigDecimal amount;

    @Override
    public String toString() {
        return "SaleResponseDTO{" +
                "id='" + id + '\'' +
                ", seller=" + seller +
                ", products=" + products +
                ", amount=" + amount +
                '}';
    }
}

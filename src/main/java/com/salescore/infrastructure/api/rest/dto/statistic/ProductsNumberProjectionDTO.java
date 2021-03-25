package com.salescore.infrastructure.api.rest.dto.statistic;

public class ProductsNumberProjectionDTO {

    public String productName;
    public Long productsNumber;

    @Override
    public String toString() {
        return "ProductsNumberProjectionDTO{" +
                "productName='" + productName + '\'' +
                ", productsNumber=" + productsNumber +
                '}';
    }
}
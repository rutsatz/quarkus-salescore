package com.salescore.dto;

import java.util.List;

public class SaleCreationDTO {

    public String sellerId;
    public List<String> productsIds;

    @Override
    public String toString() {
        return "SaleCreationDTO{" +
                "sellerId='" + sellerId + '\'' +
                ", productsIds=" + productsIds +
                '}';
    }
}

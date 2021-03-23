package com.salescore.infrastructure.api.rest.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

public class SaleCreationDTO {

    @NotBlank(message = "The seller id cannot be empty")
    public String sellerId;

    @NotNull(message = "The product list cannot be empty")
    @Size(min = 1, message = "It is necessary to inform at least one product")
    public List<String> productsIds;

    @Override
    public String toString() {
        return "SaleCreationDTO{" +
                "sellerId='" + sellerId + '\'' +
                ", productsIds=" + productsIds +
                '}';
    }
}

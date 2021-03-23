package com.salescore.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductDTO {

    public String id;

    @NotBlank(message = "The product name cannot be empty")
    public String name;

    @NotNull(message = "The product price cannot be empty")
    @DecimalMin(value = "0", inclusive = false, message = "The price of the product must be greater than zero")
    public BigDecimal price;

    @Override
    public String toString() {
        return "ProductDTO{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                '}';
    }
}

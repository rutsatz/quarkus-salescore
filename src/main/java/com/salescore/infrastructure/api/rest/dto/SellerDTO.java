package com.salescore.infrastructure.api.rest.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@JsonInclude(Include.NON_NULL)
public class SellerDTO {

    public String id;

    @NotBlank(message = "The seller name cannot be empty")
    public String name;

    @NotNull(message = "The seller's registration number cannot be empty")
    public Long registrationNumber;

    @Override
    public String toString() {
        return "SellerDTO{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", registrationNumber=" + registrationNumber +
                '}';
    }
}

package com.salescore.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class SellerDTO {

    public String id;
    public String name;
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

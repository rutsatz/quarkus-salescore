package com.salescore.infrastructure.api.rest.dto.statistic;

public class SalesNumberProjectionDTO {

    public String sellerName;
    public Long salesNumber;

    @Override
    public String toString() {
        return "SalesNumberProjectionDTO{" +
                "sellerName='" + sellerName + '\'' +
                ", salesNumber=" + salesNumber +
                '}';
    }
}
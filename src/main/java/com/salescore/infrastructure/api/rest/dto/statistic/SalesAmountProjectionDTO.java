package com.salescore.infrastructure.api.rest.dto.statistic;

import java.math.BigDecimal;

public class SalesAmountProjectionDTO {

    public String sellerName;
    public BigDecimal amount;

    @Override
    public String toString() {
        return "SalesAmountProjectionDTO{" +
                "sellerName='" + sellerName + '\'' +
                ", amount=" + amount +
                '}';
    }
}
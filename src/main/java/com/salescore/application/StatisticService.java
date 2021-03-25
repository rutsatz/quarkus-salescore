package com.salescore.application;

import com.salescore.infrastructure.api.rest.dto.statistic.ProductsNumberProjectionDTO;
import com.salescore.infrastructure.api.rest.dto.statistic.SalesAmountProjectionDTO;
import com.salescore.infrastructure.api.rest.dto.statistic.SalesNumberProjectionDTO;
import io.smallrye.mutiny.Multi;

public interface StatisticService {
    Multi<SalesNumberProjectionDTO> salesNumber();

    Multi<SalesAmountProjectionDTO> salesValue();

    Multi<ProductsNumberProjectionDTO> salesProducts();
}

package com.salescore.application;

import com.salescore.domain.model.Sale;
import com.salescore.infrastructure.api.rest.dto.SaleCreationDTO;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

public interface SaleService {
    Uni<Sale> findById(String id);

    Multi<Sale> listAll();

    Uni<Sale> create(Sale sale);

    Uni<Sale> update(SaleCreationDTO dto, String id);

    Uni<Void> delete(String id);
}

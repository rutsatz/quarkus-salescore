package com.salescore.application;

import com.salescore.domain.model.Seller;
import com.salescore.infrastructure.api.rest.dto.SellerDTO;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

public interface SellerService {
    Uni<Seller> findById(String id);

    Uni<Seller> findByRegistrationNumber(Long registrationNumber);

    Multi<Seller> listAll();

    Uni<Seller> create(Seller seller);

    Uni<Seller> update(SellerDTO dto, String id);

    Uni<Void> delete(String id);
}

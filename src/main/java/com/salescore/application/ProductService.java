package com.salescore.application;

import com.salescore.domain.model.Product;
import com.salescore.infrastructure.api.rest.dto.ProductDTO;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

public interface ProductService {
    Uni<Product> findById(String id);

    Multi<Product> listAll();

    Uni<Product> create(Product product);

    Uni<Product> update(ProductDTO dto, String id);

    Uni<Void> delete(String id);
}

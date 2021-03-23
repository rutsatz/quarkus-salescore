package com.salescore.model;

import com.salescore.dto.SellerDTO;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoEntity;
import io.smallrye.mutiny.Uni;

public class Seller extends ReactivePanacheMongoEntity {

    public String name;
    public Long registrationNumber;

    public static Uni<Seller> findByRegistrationNumber(Long registrationNumber) {
        return find("registrationNumber", registrationNumber)
                .firstResult();
    }

    /**
     * TODO: Bug relaciado ao gradle. Fix previsto para a próxima release (1.13.x)
     * https://github.com/quarkusio/quarkus/issues/15104
     */
    public Seller toEntity(SellerDTO dto) {
        this.name = dto.name;
        this.registrationNumber = dto.registrationNumber;
        return this;
    }

    public SellerDTO toDto(Seller seller) {
        var dto = new SellerDTO();
        dto.id = seller.id != null ? seller.id.toString() : null;
        dto.name = seller.name;
        dto.registrationNumber = seller.registrationNumber;
        return dto;
    }

    @Override
    public String toString() {
        return "Seller{" +
                "name='" + name + '\'' +
                ", registrationNumber=" + registrationNumber +
                ", id=" + id +
                '}';
    }
}

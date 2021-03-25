package com.salescore.infrastructure.service;

import com.salescore.application.StatisticService;
import com.salescore.domain.model.Sale;
import com.salescore.infrastructure.api.rest.dto.statistic.ProductsNumberProjectionDTO;
import com.salescore.infrastructure.api.rest.dto.statistic.SalesAmountProjectionDTO;
import com.salescore.infrastructure.api.rest.dto.statistic.SalesNumberProjectionDTO;
import io.smallrye.mutiny.Multi;
import org.bson.conversions.Bson;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.function.Supplier;

import static com.mongodb.client.model.Accumulators.sum;
import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Projections.*;
import static com.mongodb.client.model.Sorts.descending;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

/**
 * TODO: Migrate to Panache API when it becomes available (for complex operations).
 */
@ApplicationScoped
public class StatisticServiceImpl implements StatisticService {

    @Inject
    Logger log;

    Supplier<Bson> groupBySeller = () -> group("$seller.name", sum("salesNumber", 1L));
    Supplier<Bson> projectSalesNumber = () -> project(fields(excludeId(), include("salesNumber"), computed("sellerName", "$_id")));
    Supplier<Bson> sortDescendingBySales = () -> sort(descending("salesNumber"));

    Supplier<Bson> groupByAmount = () -> group("$seller.name", sum("amount", "$amount"));
    Supplier<Bson> projectAmountValue = () -> project(fields(excludeId(), include("amount"), computed("sellerName", "$_id")));
    Supplier<Bson> sortDescendingByAmount = () -> sort(descending("amount"));

    Supplier<Bson> unwindProductsArray = () -> unwind("$products");
    Supplier<Bson> groupByProduct = () -> group("$products.name", sum("productsNumber", 1L));
    Supplier<Bson> projectProductsNumber = () -> project(fields(excludeId(), include("productsNumber"), computed("productName", "$_id")));
    Supplier<Bson> sortDescendingByProducts = () -> sort(descending("productsNumber"));

    @Override
    public Multi<SalesNumberProjectionDTO> salesNumber() {
        return Sale.mongoCollection()
                .aggregate(salesNumberProjection(), SalesNumberProjectionDTO.class)
                .onItem().invoke(projection -> log.debugf("Found sales number projection %s: ", projection));
    }

    private List<Bson> salesNumberProjection() {
        return buildAggregationFor(groupBySeller, projectSalesNumber, sortDescendingBySales);
    }

    @Override
    public Multi<SalesAmountProjectionDTO> salesValue() {
        return Sale.mongoCollection()
                .aggregate(salesValueProjection(), SalesAmountProjectionDTO.class)
                .onItem().invoke(projection -> log.debugf("Found sales amount projection %s: ", projection));
    }

    private List<Bson> salesValueProjection() {
        return buildAggregationFor(groupByAmount, projectAmountValue, sortDescendingByAmount);
    }

    @Override
    public Multi<ProductsNumberProjectionDTO> salesProducts() {
        return Sale.mongoCollection()
                .aggregate(salesProductsProjection(), ProductsNumberProjectionDTO.class)
                .onItem().invoke(projection -> log.debugf("Found sales products projection %s: ", projection));
    }

    private List<Bson> salesProductsProjection() {
        return buildAggregationFor(unwindProductsArray, groupByProduct, projectProductsNumber, sortDescendingByProducts);
    }

    @SafeVarargs
    private List<Bson> buildAggregationFor(Supplier<Bson>... stages) {
        return stream(stages)
                .map(Supplier::get)
                .collect(toList());
    }
}
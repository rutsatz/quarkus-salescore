package com.salescore.infrastructure.api.rest;

import com.salescore.infrastructure.api.rest.dto.statistic.ProductsNumberProjectionDTO;
import com.salescore.infrastructure.api.rest.dto.statistic.SalesAmountProjectionDTO;
import com.salescore.infrastructure.api.rest.dto.statistic.SalesNumberProjectionDTO;
import com.salescore.application.StatisticService;
import io.smallrye.mutiny.Multi;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Tag(name = "Statistic Reports")
@Path("/api/v1/statistics")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class StatisticResource {

    @Inject
    StatisticService statisticService;

    @Operation(summary = "List of sellers by the highest number of sales")
    @GET
    @Path("/salesNumber")
    public Multi<SalesNumberProjectionDTO> salesNumber() {
        return statisticService.salesNumber();
    }

    @Operation(summary = "List of sellers by the highest total sales value")
    @GET
    @Path("/salesAmount")
    public Multi<SalesAmountProjectionDTO> salesValue() {
        return statisticService.salesValue();
    }

    @Operation(summary = "List of best selling products")
    @GET
    @Path("/salesProducts")
    public Multi<ProductsNumberProjectionDTO> salesProducts() {
        return statisticService.salesProducts();
    }

}
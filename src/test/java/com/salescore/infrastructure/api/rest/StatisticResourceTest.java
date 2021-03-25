package com.salescore.infrastructure.api.rest;

import com.salescore.application.SaleService;
import com.salescore.domain.model.Product;
import com.salescore.domain.model.Sale;
import com.salescore.domain.model.Seller;
import com.salescore.infrastructure.api.rest.dto.SaleCreationDTO;
import com.salescore.infrastructure.config.DatabaseResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.*;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;

import static io.restassured.RestAssured.given;
import static io.restassured.config.JsonConfig.jsonConfig;
import static io.restassured.path.json.config.JsonPathConfig.NumberReturnType.BIG_DECIMAL;
import static org.hamcrest.core.Is.is;

@QuarkusTest
@TestHTTPEndpoint(StatisticResource.class)
@QuarkusTestResource(DatabaseResource.class)
public class StatisticResourceTest {

    private static final Duration TIMEOUT = Duration.ofSeconds(5);

    private static Seller SELLER;

    private static Product PRODUCT;

    @Inject
    SaleService saleService;

    @BeforeAll
    public static void beforeAll() {
        var seller = defaultSeller();
        seller.persist().await().atMost(TIMEOUT);
        Assertions.assertEquals(1, Seller.count().await().atMost(TIMEOUT));
        SELLER = seller;

        var product = defaultProduct();
        product.persist().await().atMost(TIMEOUT);
        Assertions.assertEquals(1, Product.count().await().atMost(TIMEOUT));
        PRODUCT = product;
    }

    @AfterAll
    public static void afterAll() {
        Seller.deleteAll().await().atMost(TIMEOUT);
        Product.deleteAll().await().atMost(TIMEOUT);
    }

    private static Seller defaultSeller() {
        var seller = new Seller();
        seller.setName("Seller 1");
        seller.setRegistrationNumber(123L);
        return seller;
    }

    private static Product defaultProduct() {
        var product = new Product();
        product.setPrice(new BigDecimal("10.0"));
        product.setName("Product 1");
        return product;
    }

    @BeforeEach
    public void beforeEach() {
        Sale.deleteAll().await().atMost(TIMEOUT);
    }

    @Test
    public void shouldReturnStatisticForAmountOfSales() {
        Assertions.assertEquals(0, Sale.count().await().atMost(TIMEOUT));

        var sale = sale1();
        saleService.create(sale).await().atMost(TIMEOUT);
        Assertions.assertEquals(1, Sale.count().await().atMost(TIMEOUT));

        given().config(RestAssured.config()
                .jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL)))
                .when().get("/salesNumber")
                .then()
                .statusCode(200)
                .body("size()", is(1))
                .body("[0].sellerName", is(sale.getSeller().getName()))
                .body("[0].salesNumber", is(1));
    }

    @Test
    public void shouldReturnStatisticsOfTotalSoldBySeller() {
        Assertions.assertEquals(0, Sale.count().await().atMost(TIMEOUT));

        var sale = sale1();
        saleService.create(sale).await().atMost(TIMEOUT);
        Assertions.assertEquals(1, Sale.count().await().atMost(TIMEOUT));

        given().config(RestAssured.config()
                .jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL)))
                .when().get("/salesNumber")
                .then()
                .statusCode(200)
                .body("size()", is(1))
                .body("[0].sellerName", is(sale.getSeller().getName()))
                .body("[0].salesNumber", is(1));
    }

    @Test
    public void shouldReturnStatisticsOfTotalProductsSoldBySeller() {
        Assertions.assertEquals(0, Sale.count().await().atMost(TIMEOUT));

        var sale = sale1();
        saleService.create(sale).await().atMost(TIMEOUT);
        Assertions.assertEquals(1, Sale.count().await().atMost(TIMEOUT));

        given().config(RestAssured.config()
                .jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL)))
                .when().get("/salesProducts")
                .then()
                .statusCode(200)
                .body("size()", is(1))
                .body("[0].productName", is(sale.getProducts().get(0).getName()))
                .body("[0].productsNumber", is(1));
    }

    private Sale sale1() {
        var sale = new Sale();
        sale.setSeller(SELLER);
        sale.setProducts(List.of(PRODUCT));
        return sale;
    }

    private SaleCreationDTO saleCreate(String seller, List<String> products) {
        var dto = new SaleCreationDTO();
        dto.sellerId = seller;
        dto.productsIds = products;
        return dto;
    }

    private SaleCreationDTO defaultSale() {
        return saleCreate(SELLER.id.toString(), List.of(PRODUCT.id.toString()));
    }
}
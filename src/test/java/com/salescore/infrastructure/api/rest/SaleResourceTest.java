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
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;

import static io.restassured.RestAssured.given;
import static io.restassured.config.JsonConfig.jsonConfig;
import static io.restassured.path.json.config.JsonPathConfig.NumberReturnType.BIG_DECIMAL;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;

@QuarkusTest
@TestHTTPEndpoint(SaleResource.class)
@QuarkusTestResource(DatabaseResource.class)
public class SaleResourceTest {

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
    public void findById_shouldReturnCorrectSale() {
        Assertions.assertEquals(0, Sale.count().await().atMost(TIMEOUT));

        var sale = sale1();
        saleService.create(sale).await().atMost(TIMEOUT);
        Assertions.assertEquals(1, Sale.count().await().atMost(TIMEOUT));

        given().config(RestAssured.config()
                .jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL)))
                .when().get("/{id}", sale.id.toString())
                .then()
                .statusCode(200)
                .body("seller.id", is(sale.getSeller().id.toString()))
                .body("products.size()", is(1))
                .body("products[0].id", is(sale.getProducts().get(0).id.toString()))
                .body("amount", is(new BigDecimal("10.0")));
    }

    @Test
    public void findAll_shouldReturnCorrectSale() {
        Assertions.assertEquals(0, Sale.count().await().atMost(TIMEOUT));

        var sale = sale1();
        saleService.create(sale).await().atMost(TIMEOUT);
        Assertions.assertEquals(1, Sale.count().await().atMost(TIMEOUT));

        given().config(RestAssured.config()
                .jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL)))
                .when().get()
                .then()
                .statusCode(200)
                .assertThat()
                .body("size()", is(1))
                .body("[0].seller.id", is(sale.getSeller().id.toString()))
                .body("[0].products.size()", is(1))
                .body("[0].products[0].id", is(sale.getProducts().get(0).id.toString()))
                .body("[0].amount", is(new BigDecimal("10.0")));
    }

    @Test
    public void find_shouldReturnInvalidId() {
        Assertions.assertEquals(0, Sale.count().await().atMost(TIMEOUT));

        given().config(RestAssured.config()
                .jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL)))
                .contentType(ContentType.JSON)
                .when().get("/{id}", "605c2c900500846158a20bd9")
                .then()
                .statusCode(404)
                .assertThat()
                .body("size()", is(1))
                .body("[0].message", equalTo("Resource not found"));
    }

    @Test
    public void create_shouldCreateNewSale() {
        Assertions.assertEquals(0, Sale.count().await().atMost(TIMEOUT));

        given().config(RestAssured.config()
                .jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL)))
                .contentType(ContentType.JSON)
                .body(defaultSale())
                .when().post()
                .then()
                .statusCode(201)
                .assertThat()
                .header("Location", notNullValue());
    }

    @Test
    public void create_shouldReturnInvalidProductsWhenNotInformed() {
        Assertions.assertEquals(0, Sale.count().await().atMost(TIMEOUT));

        var dto = defaultSale();
        dto.productsIds = null;

        given().config(RestAssured.config()
                .jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL)))
                .contentType(ContentType.JSON)
                .body(dto)
                .when().post()
                .then()
                .statusCode(400)
                .assertThat()
                .body("size()", is(1))
                .body("[0].message", equalTo("The product list cannot be empty"));
    }

    @Test
    public void create_shouldReturnInvalidSellerWhenNotInformed() {
        Assertions.assertEquals(0, Sale.count().await().atMost(TIMEOUT));

        var dto = defaultSale();
        dto.sellerId = "";

        given().config(RestAssured.config()
                .jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL)))
                .contentType(ContentType.JSON)
                .body(dto)
                .when().post()
                .then()
                .statusCode(400)
                .assertThat()
                .body("size()", is(1))
                .body("[0].message", equalTo("The seller id cannot be empty"));
    }

    @Test
    public void update_shouldUpdateSale() {
        Assertions.assertEquals(0, Sale.count().await().atMost(TIMEOUT));

        var sale = sale1();
        sale.persist().await().atMost(TIMEOUT);
        Assertions.assertEquals(1, Sale.count().await().atMost(TIMEOUT));

        var dto = defaultSale();

        given().config(RestAssured.config()
                .jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL)))
                .contentType(ContentType.JSON)
                .body(dto)
                .when().put("/{id}", sale.id.toString())
                .then()
                .statusCode(200)
                .assertThat()
                .body("seller.id", is(sale.getSeller().id.toString()))
                .body("products.size()", is(1))
                .body("products[0].id", is(sale.getProducts().get(0).id.toString()));
    }

    @Test
    public void update_shouldReturnInvalidId() {
        Assertions.assertEquals(0, Sale.count().await().atMost(TIMEOUT));

        var dto = defaultSale();

        given().config(RestAssured.config()
                .jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL)))
                .contentType(ContentType.JSON)
                .body(dto)
                .when().put("/{id}", "605c2c900500846158a20bd9")
                .then()
                .statusCode(404)
                .assertThat()
                .body("size()", is(1))
                .body("[0].message", equalTo("Resource not found"));
    }

    @Test
    public void update_shouldReturnInvalidProductsWhenNotInformed() {
        Assertions.assertEquals(0, Sale.count().await().atMost(TIMEOUT));

        var product = sale1();
        product.persist().await().atMost(TIMEOUT);
        Assertions.assertEquals(1, Sale.count().await().atMost(TIMEOUT));

        var dto = defaultSale();
        dto.productsIds = null;

        given().config(RestAssured.config()
                .jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL)))
                .contentType(ContentType.JSON)
                .body(dto)
                .when().put("/{id}", product.id.toString())
                .then()
                .statusCode(400)
                .assertThat()
                .body("size()", is(1))
                .body("[0].message", equalTo("The product list cannot be empty"));
    }

    @Test
    public void update_shouldReturnInvalidSellerWhenNotInformed() {
        Assertions.assertEquals(0, Sale.count().await().atMost(TIMEOUT));

        var product = sale1();
        product.persist().await().atMost(TIMEOUT);
        Assertions.assertEquals(1, Sale.count().await().atMost(TIMEOUT));

        var dto = defaultSale();
        dto.sellerId = "";

        given().config(RestAssured.config()
                .jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL)))
                .contentType(ContentType.JSON)
                .body(dto)
                .when().put("/{id}", product.id.toString())
                .then()
                .statusCode(400)
                .assertThat()
                .body("size()", is(1))
                .body("[0].message", equalTo("The seller id cannot be empty"));
    }

    @Test
    public void delete_shouldDeleteSale() {
        Assertions.assertEquals(0, Sale.count().await().atMost(TIMEOUT));

        var product = sale1();
        product.persist().await().atMost(TIMEOUT);
        Assertions.assertEquals(1, Sale.count().await().atMost(TIMEOUT));

        given().config(RestAssured.config()
                .jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL)))
                .contentType(ContentType.JSON)
                .when().delete("/{id}", product.id.toString())
                .then()
                .statusCode(204);
    }

    @Test
    public void delete_shouldReturnInvalidId() {
        Assertions.assertEquals(0, Sale.count().await().atMost(TIMEOUT));

        given().config(RestAssured.config()
                .jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL)))
                .contentType(ContentType.JSON)
                .when().delete("/{id}", "605c2c900500846158a20bd9")
                .then()
                .statusCode(404)
                .assertThat()
                .body("size()", is(1))
                .body("[0].message", equalTo("Resource not found"));
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
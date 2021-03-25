package com.salescore.infrastructure.api.rest;

import com.salescore.domain.model.Product;
import com.salescore.infrastructure.api.rest.dto.ProductDTO;
import com.salescore.infrastructure.config.DatabaseResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Duration;

import static io.restassured.RestAssured.given;
import static io.restassured.config.JsonConfig.jsonConfig;
import static io.restassured.path.json.config.JsonPathConfig.NumberReturnType.BIG_DECIMAL;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;

@QuarkusTest
@TestHTTPEndpoint(ProductResource.class)
@QuarkusTestResource(DatabaseResource.class)
public class ProductResourceTest {

    private static final Duration TIMEOUT = Duration.ofSeconds(5);

    @AfterAll
    public static void afterAll() {
        Product.deleteAll().await().atMost(TIMEOUT);
    }

    @BeforeEach
    public void beforeEach() {
        Product.deleteAll().await().atMost(TIMEOUT);
    }

    @Test
    public void findById_shouldReturnCorrectProduct() {
        Assertions.assertEquals(0, Product.count().await().atMost(TIMEOUT));

        var product = defaultProduct();
        product.persist().await().atMost(TIMEOUT);
        Assertions.assertEquals(1, Product.count().await().atMost(TIMEOUT));

        given().config(RestAssured.config()
                .jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL)))
                .when().get("/{id}", product.id.toString())
                .then()
                .statusCode(200)
                .body("name", is(product.getName()))
                .body("price", is(product.getPrice()));
    }

    @Test
    public void findAll_shouldReturnCorrectProduct() {
        Assertions.assertEquals(0, Product.count().await().atMost(TIMEOUT));

        var product = defaultProduct();
        product.persist().await().atMost(TIMEOUT);
        Assertions.assertEquals(1, Product.count().await().atMost(TIMEOUT));

        given().config(RestAssured.config()
                .jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL)))
                .when().get()
                .then()
                .statusCode(200)
                .assertThat()
                .body("size()", is(1))
                .body("[0].name", is(product.getName()))
                .body("[0].price", is(product.getPrice()));
    }

    @Test
    public void find_shouldReturnInvalidId() {
        Assertions.assertEquals(0, Product.count().await().atMost(TIMEOUT));

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
    public void create_shouldCreateNewProduct() {
        Assertions.assertEquals(0, Product.count().await().atMost(TIMEOUT));

        given().config(RestAssured.config()
                .jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL)))
                .contentType(ContentType.JSON)
                .body(productCreate())
                .when().post()
                .then()
                .statusCode(201)
                .assertThat()
                .header("Location", notNullValue());
    }

    @Test
    public void create_shouldReturnInvalidPriceWhenNotInformed() {
        Assertions.assertEquals(0, Product.count().await().atMost(TIMEOUT));

        var dto = productCreate();
        dto.price = null;

        given().config(RestAssured.config()
                .jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL)))
                .contentType(ContentType.JSON)
                .body(dto)
                .when().post()
                .then()
                .statusCode(400)
                .assertThat()
                .body("size()", is(1))
                .body("[0].message", equalTo("The product price cannot be empty"));
    }


    @Test
    public void create_shouldReturnInvalidPriceForZero() {
        Assertions.assertEquals(0, Product.count().await().atMost(TIMEOUT));

        var dto = productCreate();
        dto.price = new BigDecimal("0");

        given().config(RestAssured.config()
                .jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL)))
                .contentType(ContentType.JSON)
                .body(dto)
                .when().post()
                .then()
                .statusCode(400)
                .assertThat()
                .body("size()", is(1))
                .body("[0].message", equalTo("The price of the product must be greater than zero"));
    }

    @Test
    public void update_shouldUpdateProduct() {
        Assertions.assertEquals(0, Product.count().await().atMost(TIMEOUT));

        var product = defaultProduct();
        product.persist().await().atMost(TIMEOUT);
        Assertions.assertEquals(1, Product.count().await().atMost(TIMEOUT));

        var dto = productCreate();

        given().config(RestAssured.config()
                .jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL)))
                .contentType(ContentType.JSON)
                .body(dto)
                .when().put("/{id}", product.id.toString())
                .then()
                .statusCode(200)
                .assertThat()
                .body("id", is(product.id.toString()))
                .body("name", is(dto.name))
                .body("price", is(dto.price));
    }

    @Test
    public void update_shouldReturnInvalidId() {
        Assertions.assertEquals(0, Product.count().await().atMost(TIMEOUT));

        var dto = productCreate();

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
    public void update_shouldReturnInvalidPriceWhenNotInformed() {
        Assertions.assertEquals(0, Product.count().await().atMost(TIMEOUT));

        var product = defaultProduct();
        product.persist().await().atMost(TIMEOUT);
        Assertions.assertEquals(1, Product.count().await().atMost(TIMEOUT));

        var dto = productCreate();
        dto.price = null;

        given().config(RestAssured.config()
                .jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL)))
                .contentType(ContentType.JSON)
                .body(dto)
                .when().put("/{id}", product.id.toString())
                .then()
                .statusCode(400)
                .assertThat()
                .body("size()", is(1))
                .body("[0].message", equalTo("The product price cannot be empty"));
    }

    @Test
    public void update_shouldReturnInvalidPriceForZero() {
        Assertions.assertEquals(0, Product.count().await().atMost(TIMEOUT));

        var product = defaultProduct();
        product.persist().await().atMost(TIMEOUT);
        Assertions.assertEquals(1, Product.count().await().atMost(TIMEOUT));

        var dto = productCreate();
        dto.price = new BigDecimal("0");

        given().config(RestAssured.config()
                .jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL)))
                .contentType(ContentType.JSON)
                .body(dto)
                .when().put("/{id}", product.id.toString())
                .then()
                .statusCode(400)
                .assertThat()
                .body("size()", is(1))
                .body("[0].message", equalTo("The price of the product must be greater than zero"));
    }

    @Test
    public void delete_shouldDeleteProduct() {
        Assertions.assertEquals(0, Product.count().await().atMost(TIMEOUT));

        var product = defaultProduct();
        product.persist().await().atMost(TIMEOUT);
        Assertions.assertEquals(1, Product.count().await().atMost(TIMEOUT));

        given().config(RestAssured.config()
                .jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL)))
                .contentType(ContentType.JSON)
                .when().delete("/{id}", product.id.toString())
                .then()
                .statusCode(204);
    }

    @Test
    public void delete_shouldReturnInvalidId() {
        Assertions.assertEquals(0, Product.count().await().atMost(TIMEOUT));

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

    private Product defaultProduct() {
        var product = new Product();
        product.setPrice(new BigDecimal("10.0"));
        product.setName("Product 1");
        return product;
    }

    private ProductDTO productCreate() {
        var dto = new ProductDTO();
        dto.id = "605c2c900500846158a20bd9";
        dto.name = "Mouse";
        dto.price = new BigDecimal("30.5");
        return dto;
    }
}
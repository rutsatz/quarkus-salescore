package com.salescore.infrastructure.api.rest;

import com.salescore.domain.model.Seller;
import com.salescore.infrastructure.api.rest.dto.SellerDTO;
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

import java.time.Duration;

import static io.restassured.RestAssured.given;
import static io.restassured.config.JsonConfig.jsonConfig;
import static io.restassured.path.json.config.JsonPathConfig.NumberReturnType.BIG_DECIMAL;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;

@QuarkusTest
@TestHTTPEndpoint(SellerResource.class)
@QuarkusTestResource(DatabaseResource.class)
public class SellerResourceTest {

    private static final Duration TIMEOUT = Duration.ofSeconds(5);

    @AfterAll
    public static void afterAll() {
        Seller.deleteAll().await().atMost(TIMEOUT);
    }

    @BeforeEach
    public void beforeEach() {
        Seller.deleteAll().await().atMost(TIMEOUT);
    }

    @Test
    public void findById_shouldReturnCorrectSeller() {
        Assertions.assertEquals(0, Seller.count().await().atMost(TIMEOUT));

        var seller = defaultSeller();
        seller.persist().await().atMost(TIMEOUT);
        Assertions.assertEquals(1, Seller.count().await().atMost(TIMEOUT));

        given()
                .when().get("/{id}", seller.id.toString())
                .then()
                .statusCode(200)
                .body("name", is(seller.getName()))
                .body("registrationNumber", equalTo(seller.getRegistrationNumber().intValue()));
    }

    @Test
    public void findById_shouldReturnInvalidId() {
        Assertions.assertEquals(0, Seller.count().await().atMost(TIMEOUT));

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
    public void findByRegistrationNumber_shouldReturnCorrectSeller() {
        Assertions.assertEquals(0, Seller.count().await().atMost(TIMEOUT));

        var seller = defaultSeller();
        seller.persist().await().atMost(TIMEOUT);
        Assertions.assertEquals(1, Seller.count().await().atMost(TIMEOUT));

        given()
                .queryParam("registrationNumber", seller.getRegistrationNumber())
                .when().get()
                .then()
                .statusCode(200)
                .body("name", is(seller.getName()))
                .body("registrationNumber", is(seller.getRegistrationNumber().intValue()));
    }

    @Test
    public void findByRegistrationNumber_shouldReturnInvalidRegistrationNumber() {
        Assertions.assertEquals(0, Seller.count().await().atMost(TIMEOUT));

        given()
                .contentType(ContentType.JSON)
                .queryParam("registrationNumber", 789)
                .when().get()
                .then()
                .statusCode(404)
                .assertThat()
                .body("size()", is(1))
                .body("[0].message", equalTo("Resource not found"));
    }

    @Test
    public void findAll_shouldReturnCorrectSeller() {
        Assertions.assertEquals(0, Seller.count().await().atMost(TIMEOUT));

        var seller = defaultSeller();
        seller.persist().await().atMost(TIMEOUT);
        Assertions.assertEquals(1, Seller.count().await().atMost(TIMEOUT));

        given()
                .when().get("/all")
                .then()
                .statusCode(200)
                .assertThat()
                .body("size()", is(1))
                .body("[0].name", is(seller.getName()))
                .body("[0].registrationNumber", is(seller.getRegistrationNumber().intValue()));
    }

    @Test
    public void create_shouldCreateNewSeller() {
        Assertions.assertEquals(0, Seller.count().await().atMost(TIMEOUT));

        given()
                .contentType(ContentType.JSON)
                .body(sellerCreate())
                .when().post()
                .then()
                .statusCode(201)
                .assertThat()
                .header("Location", notNullValue());
    }

    @Test
    public void create_shouldReturnInvalidRegistrationNumberWhenNotInformed() {
        Assertions.assertEquals(0, Seller.count().await().atMost(TIMEOUT));

        var dto = sellerCreate();
        dto.registrationNumber = null;

        given()
                .contentType(ContentType.JSON)
                .body(dto)
                .when().post()
                .then()
                .statusCode(400)
                .assertThat()
                .body("size()", is(1))
                .body("[0].message", equalTo("The seller's registration number cannot be empty"));
    }


    @Test
    public void create_shouldReturnInvalidName() {
        Assertions.assertEquals(0, Seller.count().await().atMost(TIMEOUT));

        var dto = sellerCreate();
        dto.name = "";

        given()
                .contentType(ContentType.JSON)
                .body(dto)
                .when().post()
                .then()
                .statusCode(400)
                .assertThat()
                .body("size()", is(1))
                .body("[0].message", equalTo("The seller name cannot be empty"));
    }

    @Test
    public void update_shouldUpdateSeller() {
        Assertions.assertEquals(0, Seller.count().await().atMost(TIMEOUT));

        var seller = defaultSeller();
        seller.persist().await().atMost(TIMEOUT);
        Assertions.assertEquals(1, Seller.count().await().atMost(TIMEOUT));

        var dto = sellerCreate();

        given()
                .contentType(ContentType.JSON)
                .body(dto)
                .when().put("/{id}", seller.id.toString())
                .then()
                .statusCode(200)
                .assertThat()
                .body("id", is(seller.id.toString()))
                .body("name", is(dto.name))
                .body("registrationNumber", is(dto.registrationNumber.intValue()));
    }

    @Test
    public void update_shouldReturnInvalidId() {
        Assertions.assertEquals(0, Seller.count().await().atMost(TIMEOUT));

        var dto = sellerCreate();

        given()
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
    public void update_shouldReturnInvalidRegistrationNumberWhenNotInformed() {
        Assertions.assertEquals(0, Seller.count().await().atMost(TIMEOUT));

        var seller = defaultSeller();
        seller.persist().await().atMost(TIMEOUT);
        Assertions.assertEquals(1, Seller.count().await().atMost(TIMEOUT));

        var dto = sellerCreate();
        dto.registrationNumber = null;

        given()
                .contentType(ContentType.JSON)
                .body(dto)
                .when().put("/{id}", seller.id.toString())
                .then()
                .statusCode(400)
                .assertThat()
                .body("size()", is(1))
                .body("[0].message", equalTo("The seller's registration number cannot be empty"));
    }

    @Test
    public void update_shouldReturnInvalidName() {
        Assertions.assertEquals(0, Seller.count().await().atMost(TIMEOUT));

        var seller = defaultSeller();
        seller.persist().await().atMost(TIMEOUT);
        Assertions.assertEquals(1, Seller.count().await().atMost(TIMEOUT));

        var dto = sellerCreate();
        dto.name = "";

        given()
                .contentType(ContentType.JSON)
                .body(dto)
                .when().put("/{id}", seller.id.toString())
                .then()
                .statusCode(400)
                .assertThat()
                .body("size()", is(1))
                .body("[0].message", equalTo("The seller name cannot be empty"));
    }

    @Test
    public void delete_shouldDeleteSeller() {
        Assertions.assertEquals(0, Seller.count().await().atMost(TIMEOUT));

        var seller = defaultSeller();
        seller.persist().await().atMost(TIMEOUT);
        Assertions.assertEquals(1, Seller.count().await().atMost(TIMEOUT));

        given()
                .contentType(ContentType.JSON)
                .when().delete("/{id}", seller.id.toString())
                .then()
                .statusCode(204);
    }

    @Test
    public void delete_shouldReturnInvalidId() {
        Assertions.assertEquals(0, Seller.count().await().atMost(TIMEOUT));

        given()
                .contentType(ContentType.JSON)
                .when().delete("/{id}", "605c2c900500846158a20bd9")
                .then()
                .statusCode(404)
                .assertThat()
                .body("size()", is(1))
                .body("[0].message", equalTo("Resource not found"));
    }

    private Seller defaultSeller() {
        var seller = new Seller();
        seller.setName("Seller 1");
        seller.setRegistrationNumber(123L);
        return seller;
    }

    private SellerDTO sellerCreate() {
        var dto = new SellerDTO();
        dto.id = "605c2c900500846158a20bd9";
        dto.name = "Seller 2";
        dto.registrationNumber = 456L;
        return dto;
    }
}
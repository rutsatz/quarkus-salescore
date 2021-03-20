package com.salescore.resource;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class SellerResourceTest {

    @Test
    public void testHelloEndpoint() {
        given()
          .when().get("/api/v1/seller")
          .then()
             .statusCode(200);
    }

}
package com.czosnek;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles({"local"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthorsControllerIT {
  @LocalServerPort int port;

  @BeforeEach
  public void setUp() {
    RestAssured.port = port;
  }

  @Test
  public void shouldSaveNewAuthorReturn200AndAuthorId() {
    given()
        .header("Content-Type", "application/json")
        .body(
            """
                    {
                      "firstName": "Tomasz",
                      "lastName": "Nowak",
                      "age": 50
                    }
                    """)
        .when()
        .post("/authors")
        .then()
        .statusCode(200)
        .body("id", is(notNullValue()));
  }
}

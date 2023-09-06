package com.czosnek;

import static com.czosnek.jooq.Tables.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import io.restassured.RestAssured;
import org.jooq.DSLContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles({"local"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthorsControllerIT {
  @LocalServerPort int port;
  @Autowired DSLContext context;

  @BeforeEach
  public void setUp() {
    RestAssured.port = port;
    context.deleteFrom(AUTHORS).execute();
    context.deleteFrom(BOOKS).execute();
    context.deleteFrom(AUTHORS_TO_BOOKS).execute();
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

  @Test
  public void shouldSaveNewAuthorWithBookReturn200AndBookId() {
    given()
        .header("Content-Type", "application/json")
        .body(
            """
                            {
                              "firstName": "Tomasz",
                              "lastName": "Nowak",
                              "age": 50,
                              "books": [
                                {
                                  "title": "Some Title",
                                  "isbn": "978-3-16-148410-0",
                                  "published": "2007-12-03",
                                  "genre": "Fantasy"
                                }
                              ]
                            }
                            """)
        .when()
        .post("/authors")
        .then()
        .statusCode(200)
        .body("books[0].id", is(notNullValue()));
  }

  @Test
  public void shouldReturnSavedAuthors() {
    given()
        .header("Content-Type", "application/json")
        .body(
            """
                            {
                              "firstName": "Janusz",
                              "lastName": "Anon",
                              "age": 50
                            }
                            """)
        .when()
        .post("/authors")
        .then()
        .statusCode(200);
    given()
        .header("Content-Type", "application/json")
        .queryParam("lastId", 0)
        .queryParam("limit", 10)
        .when()
        .get("/authors")
        .then()
        .statusCode(200)
        .body("authors[0].firstName", equalTo("Janusz"));
  }
}

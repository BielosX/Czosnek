package com.czosnek;

import static com.czosnek.jooq.Tables.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import java.io.IOException;
import org.jooq.DSLContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles({"local"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthorsControllerIT {
  @LocalServerPort int port;
  @Autowired DSLContext context;

  @Value("classpath:author-no-books.json")
  private Resource authorNoBooks;

  @Value("classpath:author-one-book.json")
  private Resource authorOneBook;

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
        .body("id", is(notNullValue()))
        .and()
        .body("firstName", equalTo("Tomasz"))
        .and()
        .body("lastName", equalTo("Nowak"))
        .and()
        .body("age", equalTo(50));
  }

  @Test
  public void shouldSaveNewAuthorWithBookReturn200AndBookId() throws IOException {
    given()
        .header("Content-Type", "application/json")
        .body(authorOneBook.getFile())
        .when()
        .post("/authors")
        .then()
        .statusCode(200)
        .body("books[0].id", is(notNullValue()));
  }

  @Test
  public void shouldReturnSavedAuthors() throws IOException {
    given()
        .header("Content-Type", "application/json")
        .body(authorNoBooks.getFile())
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

  @Test
  public void shouldReturnAuthorWithBooksDetails() throws IOException {
    int authorId =
        given()
            .header("Content-Type", "application/json")
            .body(authorOneBook.getFile())
            .when()
            .post("/authors")
            .body()
            .jsonPath()
            .getInt("id");
    given()
        .header("Content-Type", "application/json")
        .pathParam("authorId", authorId)
        .when()
        .get("/authors/{authorId}")
        .then()
        .statusCode(200)
        .body("id", equalTo(authorId))
        .and()
        .body("lastName", equalTo("Nowak"))
        .and()
        .body("books[0].title", equalTo("Some Title"));
  }

  @Test
  public void shouldDeleteAuthorById() throws IOException {
    int authorId =
        given()
            .header("Content-Type", "application/json")
            .body(authorNoBooks.getFile())
            .when()
            .post("/authors")
            .body()
            .jsonPath()
            .getInt("id");
    given()
        .pathParam("authorId", authorId)
        .when()
        .delete("/authors/{authorId}")
        .then()
        .statusCode(204);
    given()
        .pathParam("authorId", authorId)
        .when()
        .get("/authors/{authorId}")
        .then()
        .statusCode(404);
  }

  @Test
  public void shouldDetachAuthorFromBook() throws IOException {
    JsonPath jsonPath =
        given()
            .header("Content-Type", "application/json")
            .body(authorOneBook.getFile())
            .when()
            .post("/authors")
            .body()
            .jsonPath();
    int authorId = jsonPath.getInt("id");
    int bookId = jsonPath.getInt("books[0].id");
    given()
        .pathParam("authorId", authorId)
        .pathParam("bookId", bookId)
        .when()
        .delete("/authors/{authorId}/books/{bookId}")
        .then()
        .statusCode(204);
    given()
        .header("Content-Type", "application/json")
        .pathParam("authorId", authorId)
        .when()
        .get("/authors/{authorId}")
        .then()
        .statusCode(200)
        .body("books", is(empty()));
  }
}

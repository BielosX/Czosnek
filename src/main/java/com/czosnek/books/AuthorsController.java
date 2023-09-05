package com.czosnek.books;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthorsController {
  private final BooksService booksService;

  @PostMapping(
      path = "/authors",
      consumes = {APPLICATION_JSON_VALUE})
  public AuthorWithBooks addAuthor(@Valid @RequestBody AddAuthorRequest request) {
    return booksService.addAuthor(request);
  }

  @GetMapping(
      path = "/authors",
      produces = {APPLICATION_JSON_VALUE})
  public GetAuthorsResult getAuthors(
      @RequestParam(defaultValue = "0") @Min(0) int lastId,
      @RequestParam(defaultValue = "10") @Min(1) @Max(100) int limit) {
    return booksService.getAuthors(lastId, limit);
  }

  @GetMapping(
      path = "/authors/{authorId}",
      produces = {APPLICATION_JSON_VALUE})
  public ResponseEntity<AuthorWithBooks> getAuthorById(@PathVariable int authorId) {
    return booksService
        .getAuthorById(authorId)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }
}

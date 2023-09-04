package com.czosnek.books;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthorsController {
  private final BooksService booksService;

  @PostMapping(
      path = "/authors",
      consumes = {APPLICATION_JSON_VALUE})
  public AddAuthorResponse addAuthor(@Valid @RequestBody AddAuthorRequest request) {
    return booksService.addAuthor(request);
  }
}

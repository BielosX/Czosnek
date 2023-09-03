package com.czosnek.books;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.czosnek.jooq.tables.records.AuthorsRecord;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthorsController {
  private final AuthorsRepository repository;

  @PostMapping(
      path = "/authors",
      consumes = {APPLICATION_JSON_VALUE})
  public AddAuthorResponse addAuthor(@RequestBody AddAuthorRequest request) {
    LocalDateTime time = LocalDateTime.now();
    AuthorsRecord record = new AuthorsRecord();
    record.setAge(request.age());
    record.setFirstName(request.firstName());
    record.setLastName(request.lastName());
    record.setCreated(time);
    record.setUpdated(time);
    AuthorsRecord result = repository.addAuthor(record);
    return new AddAuthorResponse(
        result.getId(),
        result.getFirstName(),
        result.getLastName(),
        result.getAge(),
        result.getCreated(),
        result.getUpdated());
  }
}

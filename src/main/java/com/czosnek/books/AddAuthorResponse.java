package com.czosnek.books;

import java.time.LocalDateTime;
import java.util.List;

public record AddAuthorResponse(
    int id,
    String firstName,
    String lastName,
    int age,
    List<AddBookResponse> books,
    LocalDateTime created,
    LocalDateTime updated) {}

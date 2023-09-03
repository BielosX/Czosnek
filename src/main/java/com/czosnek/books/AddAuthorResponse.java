package com.czosnek.books;

import java.time.LocalDateTime;

public record AddAuthorResponse(
    int id,
    String firstName,
    String lastName,
    int age,
    LocalDateTime created,
    LocalDateTime updated) {}

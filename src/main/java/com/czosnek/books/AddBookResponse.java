package com.czosnek.books;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record AddBookResponse(
    int id,
    String title,
    String isbn,
    LocalDate published,
    String genre,
    LocalDateTime created,
    LocalDateTime updated) {}

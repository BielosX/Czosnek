package com.czosnek.books;

import java.time.LocalDateTime;
import java.util.List;

public record AuthorWithBooks(
    int id,
    String firstName,
    String lastName,
    int age,
    List<Book> books,
    LocalDateTime created,
    LocalDateTime updated) {}

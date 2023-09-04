package com.czosnek.books;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record AddBookRequest(
    @NotNull String title,
    @NotNull String isbn,
    @NotNull LocalDate published,
    @NotNull String genre) {}

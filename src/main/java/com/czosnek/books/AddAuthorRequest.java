package com.czosnek.books;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record AddAuthorRequest(
    @NotNull String firstName,
    @NotNull String lastName,
    @NotNull int age,
    @Size(max = 20) List<AddBookRequest> books) {}

package com.czosnek.books;

import java.time.LocalDate;

public record Author(
    int id, String firstName, String lastName, int age, LocalDate created, LocalDate updated) {}

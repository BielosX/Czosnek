package com.czosnek.books;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

public record GetAuthorsResult(List<Author> authors, @JsonInclude(NON_NULL) Integer lastId) {}

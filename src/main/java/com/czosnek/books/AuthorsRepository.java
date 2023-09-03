package com.czosnek.books;

import static com.czosnek.jooq.Tables.AUTHORS;

import com.czosnek.jooq.tables.records.AuthorsRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthorsRepository {
  private final DSLContext context;

  AuthorsRecord addAuthor(AuthorsRecord record) {
    return context.insertInto(AUTHORS).set(record).returning().fetchOne();
  }
}

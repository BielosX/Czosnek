package com.czosnek.books;

import static com.czosnek.jooq.Tables.*;

import com.czosnek.jooq.tables.records.AuthorsRecord;
import com.czosnek.jooq.tables.records.AuthorsToBooksRecord;
import com.czosnek.jooq.tables.records.BooksRecord;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.InsertQuery;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BooksService {
  private final DSLContext context;

  private static AuthorsRecord createAuthorRecord(AddAuthorRequest request) {
    LocalDateTime time = LocalDateTime.now();
    AuthorsRecord authorRecord = new AuthorsRecord();
    authorRecord.setAge(request.age());
    authorRecord.setFirstName(request.firstName());
    authorRecord.setLastName(request.lastName());
    authorRecord.setCreated(time);
    authorRecord.setUpdated(time);
    return authorRecord;
  }

  private static BooksRecord createBookRecord(AddBookRequest request) {
    LocalDateTime time = LocalDateTime.now();
    BooksRecord booksRecord = new BooksRecord();
    booksRecord.setCreated(time);
    booksRecord.setUpdated(time);
    booksRecord.setTitle(request.title());
    booksRecord.setIsbn(request.isbn());
    booksRecord.setPublished(request.published());
    booksRecord.setGenre(request.genre());
    return booksRecord;
  }

  private static BooksRecord insertBook(DSLContext dsl, BooksRecord record) {
    LocalDateTime timestamp = LocalDateTime.now();
    InsertQuery<BooksRecord> query = dsl.insertQuery(BOOKS);
    query.addRecord(record);
    query.onConflict(BOOKS.ISBN);
    query.onDuplicateKeyUpdate(true);
    query.addValueForUpdate(BOOKS.UPDATED, timestamp);
    query.setReturning();
    query.execute();
    return query.getReturnedRecord();
  }

  private static Book booksRecordToBook(BooksRecord record) {
    return record.into(Book.class);
  }

  private static AuthorWithBooks authorRecordToAuthorWithBooks(
      AuthorsRecord record, List<Book> books) {
    return new AuthorWithBooks(
        record.getId(),
        record.getFirstName(),
        record.getLastName(),
        record.getAge(),
        books,
        record.getCreated(),
        record.getUpdated());
  }

  private static void insertAuthorToBook(
      DSLContext context, AuthorsRecord authorsRecord, BooksRecord booksRecord) {
    AuthorsToBooksRecord record = new AuthorsToBooksRecord();
    record.setAuthorId(authorsRecord.getId());
    record.setBookId(booksRecord.getId());
    LocalDateTime timestamp = LocalDateTime.now();
    record.setCreated(timestamp);
    record.setUpdated(timestamp);
    context.insertInto(AUTHORS_TO_BOOKS).set(record).execute();
  }

  public AuthorWithBooks addAuthor(AddAuthorRequest request) {
    AuthorsRecord authorRecord = createAuthorRecord(request);
    List<BooksRecord> booksRecords =
        request.books().stream().map(BooksService::createBookRecord).toList();
    return context.transactionResult(
        config -> {
          DSLContext dsl = config.dsl();
          AuthorsRecord authorResult =
              dsl.insertInto(AUTHORS)
                  .set(authorRecord)
                  .returningResult(DSL.asterisk())
                  .fetchOneInto(AuthorsRecord.class);
          List<BooksRecord> booksResult =
              booksRecords.stream().map(booksRecord -> insertBook(dsl, booksRecord)).toList();
          assert authorResult != null;
          booksResult.forEach(book -> insertAuthorToBook(dsl, authorResult, book));
          List<Book> bookResponses =
              booksResult.stream().map(BooksService::booksRecordToBook).toList();
          return authorRecordToAuthorWithBooks(authorResult, bookResponses);
        });
  }

  public GetAuthorsResult getAuthors(int lastId, int limit) {
    List<Author> authors =
        context
            .selectFrom(AUTHORS)
            .orderBy(AUTHORS.ID)
            .seek(lastId)
            .limit(limit)
            .fetchInto(Author.class);
    int size = authors.size();
    Integer newLastId = null;
    if (size > 0) {
      newLastId = authors.get(size - 1).id();
    }
    return new GetAuthorsResult(authors, newLastId);
  }

  private record AuthorAndBook(AuthorsRecord authorRecord, BooksRecord booksRecord) {}

  public Optional<AuthorWithBooks> getAuthorById(int authorId) {
    List<AuthorAndBook> authorAndBooks =
        context
            .select(AUTHORS.fields())
            .select(BOOKS.fields())
            .from(AUTHORS)
            .join(AUTHORS_TO_BOOKS)
            .on(AUTHORS.ID.eq(AUTHORS_TO_BOOKS.AUTHOR_ID))
            .join(BOOKS)
            .on(BOOKS.ID.eq(AUTHORS_TO_BOOKS.BOOK_ID))
            .where(AUTHORS.ID.eq(authorId))
            .fetch(
                record -> {
                  AuthorsRecord authorsRecord = record.into(AUTHORS).into(AuthorsRecord.class);
                  BooksRecord booksRecord = record.into(BOOKS).into(BooksRecord.class);
                  return new AuthorAndBook(authorsRecord, booksRecord);
                });
    if (!authorAndBooks.isEmpty()) {
      AuthorsRecord author = authorAndBooks.get(0).authorRecord();
      List<Book> books =
          authorAndBooks.stream()
              .map(AuthorAndBook::booksRecord)
              .map(BooksService::booksRecordToBook)
              .toList();
      return Optional.of(authorRecordToAuthorWithBooks(author, books));
    } else {
      return Optional.empty();
    }
  }
}

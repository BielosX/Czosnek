CREATE TABLE authors (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    age INTEGER NOT NULL,
    created TIMESTAMP,
    updated TIMESTAMP
);

CREATE TABLE books (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    isbn VARCHAR(255) NOT NULL,
    published DATE NOT NULL,
    genre VARCHAR(255),
    created TIMESTAMP,
    updated TIMESTAMP
);

CREATE TABLE authors_to_books (
    id SERIAL PRIMARY KEY,
    author_id INTEGER REFERENCES authors(id),
    book_id INTEGER REFERENCES books(id),
    created TIMESTAMP,
    updated TIMESTAMP
);
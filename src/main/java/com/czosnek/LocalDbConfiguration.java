package com.czosnek;

import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;

@Slf4j
@Configuration
@Profile({"local"})
@RequiredArgsConstructor
public class LocalDbConfiguration {

  @Bean
  public PostgreSQLContainer<?> postgreSQLContainer() {
    String IMAGE_NAME = "postgres:15.4-alpine";
    PostgreSQLContainer<?> container = new PostgreSQLContainer<>(IMAGE_NAME);
    container.setCommand("postgres", "-c", "fsync=off", "-c", "log_statement=all");
    container.start();
    Slf4jLogConsumer consumer = new Slf4jLogConsumer(log);
    container.followOutput(consumer);
    return container;
  }

  @Bean
  public DataSource postgresDataSource(PostgreSQLContainer<?> container) {
    PGSimpleDataSource dataSource = new PGSimpleDataSource();
    dataSource.setUrl(container.getJdbcUrl());
    dataSource.setPassword(container.getPassword());
    dataSource.setUser(container.getUsername());
    return dataSource;
  }
}

package com.czosnek;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;

import javax.sql.DataSource;

import static java.util.Collections.singletonList;

@Slf4j
@Configuration
@Profile({"local"})
@RequiredArgsConstructor
public class LocalDbConfiguration {

  @Bean
  public PostgreSQLContainer<?> postgreSQLContainer() {
    String IMAGE_NAME = "postgres:15.4-alpine";
    PostgreSQLContainer<?> container = new PostgreSQLContainer<>(IMAGE_NAME);
    container.start();
    Slf4jLogConsumer consumer = new Slf4jLogConsumer(log);
    container.setLogConsumers(singletonList(consumer));
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

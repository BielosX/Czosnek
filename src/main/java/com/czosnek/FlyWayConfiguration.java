package com.czosnek;

import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlyWayConfiguration {

  @Bean
  public Flyway flywayMigration(HikariDataSource dataSource) {
    Flyway flyway = Flyway.configure().dataSource(dataSource).load();
    flyway.migrate();
    return flyway;
  }
}

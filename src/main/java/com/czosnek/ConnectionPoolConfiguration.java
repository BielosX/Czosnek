package com.czosnek;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConnectionPoolConfiguration {
  @Bean
  public HikariDataSource hikariDataSource(DataSource dataSource) {
    HikariConfig config = new HikariConfig();
    config.setDataSource(dataSource);
    return new HikariDataSource(config);
  }
}

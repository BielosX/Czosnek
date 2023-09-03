package com.czosnek;

import com.zaxxer.hikari.HikariDataSource;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JooqConfiguration {
  @Bean
  public DSLContext dslContext(HikariDataSource dataSource) {
    return DSL.using(dataSource, SQLDialect.POSTGRES);
  }
}

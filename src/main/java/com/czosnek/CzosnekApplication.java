package com.czosnek;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jooq.JooqAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(
    exclude = {
      SecurityAutoConfiguration.class,
      DataSourceAutoConfiguration.class,
      JooqAutoConfiguration.class
    })
public class CzosnekApplication {

  public static void main(String[] args) {
    SpringApplication.run(CzosnekApplication.class, args);
  }
}

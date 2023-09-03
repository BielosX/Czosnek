package com.czosnek;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jooq.JooqAutoConfiguration;

@SpringBootApplication(exclude = {JooqAutoConfiguration.class})
public class CzosnekApplication {

  public static void main(String[] args) {
    SpringApplication.run(CzosnekApplication.class, args);
  }
}

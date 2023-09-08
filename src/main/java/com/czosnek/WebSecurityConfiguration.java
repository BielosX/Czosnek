package com.czosnek;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration {

  @Bean
  public SecurityFilterChain securityFilterChain(
      HttpSecurity http,
      JwtAuthorizationFilter filter,
      JwtSuccessHandler successHandler,
      AuthEntryPoint authEntryPoint,
      AuthFailureHandler failureHandler)
      throws Exception {
    http.authorizeHttpRequests(
            authorize ->
                authorize.requestMatchers("/authenticate").permitAll().anyRequest().authenticated())
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .csrf(AbstractHttpConfigurer::disable)
        .exceptionHandling(handling -> handling.authenticationEntryPoint(authEntryPoint))
        .formLogin(
            login ->
                login
                    .passwordParameter("password")
                    .usernameParameter("username")
                    .loginPage("/authenticate")
                    .successHandler(successHandler)
                    .failureHandler(failureHandler));
    http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }
}

package com.czosnek;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import io.github.jopenlibs.vault.Vault;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtSuccessHandler implements AuthenticationSuccessHandler {
  private static final String VAULT_PATH = "secret/jwt";
  private final Vault vault;

  @Override
  @SneakyThrows
  public void onAuthenticationSuccess(
      HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
    Instant timestamp = Instant.now();
    String secret =
        Optional.ofNullable(vault.logical().read(VAULT_PATH).getData().get("value")).orElseThrow();
    Algorithm algorithm = Algorithm.HMAC256(secret);
    String token =
        JWT.create()
            .withIssuedAt(timestamp)
            .withSubject(authentication.getName())
            .withExpiresAt(timestamp.plus(Duration.ofHours(1)))
            .sign(algorithm);
    response.setStatus(200);
    response.getWriter().print(token);
  }
}

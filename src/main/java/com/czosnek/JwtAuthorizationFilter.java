package com.czosnek;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import io.github.jopenlibs.vault.Vault;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtAuthorizationFilter implements Filter {
  private static final String SECRET_PATH = "secret/jwt";
  private final Vault vault;
  private final UserDetailsService userDetailsService;

  @Override
  @SneakyThrows
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
    HttpServletRequest servletRequest = (HttpServletRequest) request;
    HttpServletResponse servletResponse = (HttpServletResponse) response;
    String authorization = servletRequest.getHeader("Authorization");
    if (authorization != null) {
      List<String> split = Arrays.stream(authorization.split("\\s+")).map(String::trim).toList();
      if (split.size() == 2 && split.get(0).equals("Bearer")) {
        String token = split.get(1);
        String secret =
            Optional.ofNullable(vault.logical().read(SECRET_PATH).getData().get("value"))
                .orElseThrow();
        String subject = JWT.decode(token).getSubject();
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret)).withSubject(subject).build();
        try {
          verifier.verify(token);
          UserDetails userDetails = userDetailsService.loadUserByUsername(subject);
          Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
          if (authentication == null) {
            UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                    userDetails.getUsername(),
                    userDetails.getPassword(),
                    userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
          }
        } catch (Exception e) {
          servletResponse.setStatus(403);
          servletResponse.getWriter().print(e.getMessage());
          return;
        }
      } else {
        servletResponse.setStatus(403);
        servletResponse.getWriter().print("Bearer token required");
      }
    }
    chain.doFilter(request, response);
  }
}

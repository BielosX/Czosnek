package com.czosnek;

import jakarta.servlet.*;
import java.io.IOException;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthorizationFilter implements Filter {
  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    chain.doFilter(request, response);
  }
}

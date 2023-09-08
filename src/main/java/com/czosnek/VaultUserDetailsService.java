package com.czosnek;

import io.github.jopenlibs.vault.Vault;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VaultUserDetailsService implements UserDetailsService {
  private static final String VAULT_PATH = "secret/%s";
  private final Vault vault;

  @Override
  @SneakyThrows
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    String vaultKey = String.format(VAULT_PATH, username);
    String password =
        Optional.ofNullable(vault.logical().read(vaultKey).getData().get("value"))
            .orElseThrow(
                () -> new UsernameNotFoundException(String.format("User %s not found", username)));
    return new User(username, password, List.of(new SimpleGrantedAuthority("SERVICE")));
  }
}

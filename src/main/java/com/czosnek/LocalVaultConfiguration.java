package com.czosnek;

import io.github.jopenlibs.vault.Vault;
import io.github.jopenlibs.vault.VaultConfig;
import io.github.jopenlibs.vault.VaultException;
import java.io.IOException;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.vault.VaultContainer;

@Slf4j
@Profile({"local"})
@Configuration
public class LocalVaultConfiguration {
  private static final String IMAGE_NAME = "hashicorp/vault:1.14";

  @Bean
  public ConstVaultTokenProvider constVaultTokenProvider() {
    return new ConstVaultTokenProvider();
  }

  // https://docs.spring.io/spring-security/reference/features/authentication/password-storage.html#authentication-password-storage-configuration
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(10);
  }

  @Bean
  public Vault vault(VaultContainer<?> container, ConstVaultTokenProvider tokenProvider)
      throws VaultException {
    VaultConfig config =
        new VaultConfig()
            .address(container.getHttpHostAddress())
            .token(tokenProvider.getToken())
            .build();
    return Vault.create(config);
  }

  @Bean
  public VaultContainer<?> vaultContainer(VaultTokenProvider provider, PasswordEncoder encoder)
      throws IOException, InterruptedException {
    VaultContainer<?> container = new VaultContainer<>(IMAGE_NAME);
    Map<String, String> secrets =
        Map.of(
            "demo-api-user",
            encoder.encode("6e58aeca-0516-4076-ba7b-f375ae296928"),
            "jwt",
            "4c9d2072-fee5-4001-b3ea-d75ec6f8d22e");
    container.withVaultToken(provider.getToken());
    StringBuilder command = new StringBuilder();
    command.append("vault secrets enable kv-v2");
    secrets.forEach(
        (key, value) ->
            command.append(
                String.format(" && vault kv put -mount=secret %s value='%s'", key, value)));
    container.start();
    String stdOut = container.execInContainer("/bin/sh", "-c", command.toString()).getStdout();
    log.info("Secrets setup out: {}", stdOut);
    Slf4jLogConsumer consumer = new Slf4jLogConsumer(log);
    container.followOutput(consumer);
    return container;
  }
}

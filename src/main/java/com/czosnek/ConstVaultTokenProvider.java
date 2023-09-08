package com.czosnek;

import java.util.UUID;
import lombok.Getter;

@Getter
public class ConstVaultTokenProvider implements VaultTokenProvider {
  private final String token;

  public ConstVaultTokenProvider() {
    this.token = UUID.randomUUID().toString();
  }
}

package capital.clix.los.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import com.bettercloud.vault.Vault;
import com.bettercloud.vault.VaultConfig;
import com.bettercloud.vault.VaultException;

@Component("vaultConfiguration")
public class VaultConfiguration {

  private static final Logger LOG = LoggerFactory.getLogger(VaultConfiguration.class);

  private Vault vault = null;

  @Value("${vault.url}")
  private String VAULT_URL;

  @Value("${vault.token}")
  private String VAULT_TOKEN;

  @Value("${vault.folder}")
  private String VAULT_FOLDER;

  @Bean
  public Vault getVault() throws VaultException {
    if (vault == null) {
      VaultConfig config = new VaultConfig().address(VAULT_URL).token(VAULT_TOKEN).build();
      vault = new Vault(config);
    }
    return vault;
  }
}

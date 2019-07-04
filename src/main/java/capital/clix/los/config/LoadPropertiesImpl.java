package capital.clix.los.config;

import static capital.clix.los.constants.VaultConstants.VAULT_VALUE;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.bettercloud.vault.Vault;
import com.bettercloud.vault.VaultException;
import capital.clix.bean.MasterProperty;
import capital.clix.los.couchbaseIntegration.ICouchBaseIntegration;
import capital.clix.los.entity.ConfigProperty;
import capital.clix.los.enums.Property;
import capital.clix.startup.impl.UtilStaticBoot;

@Component
public class LoadPropertiesImpl extends UtilStaticBoot {

  @Autowired
  private Vault vault;
  @Autowired
  @Qualifier("couchBaseIntegrationImpl")
  private ICouchBaseIntegration couchBaseEgestorImpl;
  @Value("${vault.folder}")
  private String VAULT_FOLDER;
  private static final Logger LOG = LoggerFactory.getLogger(LoadPropertiesImpl.class);

  @PostConstruct
  public void loadAll() {
    loadAllCache(true);
  }

  @Override
  public void loadAllCache(boolean withVault) {
    loadParentProperties(withVault);
  }

  @Override
  public void getVaultProperties(Map<String, String> propCache) {
    LOG.info("loading vaultCache...");
    for (Property property : EnumSet.allOf(Property.class)) {
      String value = null;
      try {
        value = vault.logical().read(VAULT_FOLDER + property.getName()).getData().get(VAULT_VALUE);
      } catch (VaultException e) {
        // LOG.error("Caching of vault properties failed due to exception {}", e);
        // throw new NotificationException(ErrorCode.CACHE_ERROR, "Unable to load Vault data in
        // cache",
        // null, e);
      }
      propCache.put(property.getName(), value);

    }
    LOG.info("loaded VaultCache...");
  }

  @Override
  public List<MasterProperty> getMasterProperties() {
    ConfigProperty configProperty =
        couchBaseEgestorImpl.getEntityById("CONFIG_PROPERTY", ConfigProperty.class);
    return configProperty.getProperties();
  }

}

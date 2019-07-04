package capital.clix.los.config;

import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.couchbase.config.AbstractCouchbaseConfiguration;
import org.springframework.data.couchbase.core.CouchbaseTemplate;
import org.springframework.data.couchbase.core.WriteResultChecking;
import org.springframework.data.couchbase.repository.config.EnableCouchbaseRepositories;
import com.bettercloud.vault.Vault;
import com.couchbase.client.java.env.CouchbaseEnvironment;
import com.couchbase.client.java.env.DefaultCouchbaseEnvironment;
import capital.clix.los.constants.VaultConstants;

@Configuration
@DependsOn("vaultConfiguration")
@EnableCouchbaseRepositories
public class CouchbaseConfig extends AbstractCouchbaseConfiguration {

  @Autowired
  Vault vault;

  @Value("${vault.folder}")
  private String VAULT_FOLDER;

  @Override
  protected CouchbaseEnvironment getEnvironment() {
    CouchbaseEnvironment env = DefaultCouchbaseEnvironment.builder().connectTimeout(200000)
        .kvTimeout(100000).queryTimeout(100000).viewTimeout(100000).build();

    return env;
  }

  @Override
  protected List<String> getBootstrapHosts() {

    try {
      return Arrays
          .asList(vault.logical().read(VAULT_FOLDER + VaultConstants.VAULT_COUCHBASE_DB_URL)
              .getData().get(VaultConstants.VAULT_VALUE).split(","));
      // final String couchDbURL = "http://172.31.3.34:8091";

      // return Arrays.asList(couchDbURL);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return null;
  }

  @Override
  public CouchbaseTemplate couchbaseTemplate() throws Exception {
    CouchbaseTemplate template = super.couchbaseTemplate();
    template.setWriteResultChecking(WriteResultChecking.EXCEPTION);
    return template;
  }

  @Override
  protected String getBucketName() {

    try {
      final String couchDbBucket =
          vault.logical().read(VAULT_FOLDER + VaultConstants.VAULT_COUCHBASE_DB_BUCKET).getData()
              .get(VaultConstants.VAULT_VALUE);

      return couchDbBucket;
    } catch (Exception ex) {

    }
    return null;
  }

  @Override
  protected String getBucketPassword() {

    try {
      final String couchDbBucketPassword =
          vault.logical().read(VAULT_FOLDER + VaultConstants.VAULT_COUCHBASE_DB_BUCKET_PASSWORD)
              .getData().get(VaultConstants.VAULT_VALUE);

      return couchDbBucketPassword;
    } catch (Exception ex) {

    }
    return null;
  }
  


}

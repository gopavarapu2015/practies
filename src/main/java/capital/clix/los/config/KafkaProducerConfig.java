package capital.clix.los.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.stereotype.Component;
import com.bettercloud.vault.Vault;
import capital.clix.config.KafkaConfig;
import capital.clix.los.constants.VaultConstants;
import capital.clix.los.enums.Property;

@DependsOn("vaultConfiguration")
@Component
public class KafkaProducerConfig extends KafkaConfig {

  @Autowired
  private Vault vault;
  @Value("${vault.folder}")
  private String VAULT_FOLDER;

  @Override
  public ProducerFactory<String, String> kafkaProducerFactory() {
    try {
      return super.createProducerFactory(
          vault.logical().read(VAULT_FOLDER + Property.KAFKA_BOOTSTRAP_URL.getName()).getData()
              .get(VaultConstants.VAULT_VALUE));
      // return super.createProducerFactory("localhost:9092");
    } catch (Exception e) {

      e.printStackTrace();
    }
    return null;
  }

}

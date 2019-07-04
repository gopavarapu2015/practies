package capital.clix.los.config;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import com.bettercloud.vault.Vault;
import capital.clix.los.constants.VaultConstants;
import capital.clix.los.enums.Property;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {

  @Autowired
  private Vault vault;

  @Value("${vault.folder}")
  private String VAULT_FOLDER;

  @Bean
  public Map<String, Object> consumerConfigs() {
    Map<String, Object> props = new HashMap<>();
    try {
      // System.out.println(ConfigUtil.getPropertyValue(Property.KAFKA_BOOTSTRAP_URL,
      // String.class));
      props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
          vault.logical().read(VAULT_FOLDER + Property.KAFKA_BOOTSTRAP_URL.getName()).getData()
              .get(VaultConstants.VAULT_VALUE));
      // props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

      props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
      props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
      props.put(ConsumerConfig.GROUP_ID_CONFIG, "foo");
      props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    } catch (Exception e) {
      e.printStackTrace();
    }
    return props;
  }

  @Bean
  public ConsumerFactory<String, String> consumerFactory() {
    return new DefaultKafkaConsumerFactory<>(consumerConfigs());
  }

  @Bean
  public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> kafkaListenerContainerFactory() {

    ConcurrentKafkaListenerContainerFactory<String, String> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactory());
    return factory;
  }

}

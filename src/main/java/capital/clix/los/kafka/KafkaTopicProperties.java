package capital.clix.los.kafka;

import javax.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component("kafkaTopicProperties")
public class KafkaTopicProperties {

  private String topic;

  @PostConstruct
  public void setTopic() {
    topic = "WE-LOS-SYNC";
  }

  public String getTopic() {
    return topic;
  }

  public void setTopic(String topic) {
    this.topic = "testing";
  }
}

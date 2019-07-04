package capital.clix.los.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import capital.clix.los.bean.LosSyncDto;
import capital.clix.los.couchbaseIntegration.ICouchBaseIntegration;
import capital.clix.util.JsonMarshallingUtil;

@Service
public class KafkaConsumer {

  private static final Logger LOG = LoggerFactory.getLogger(KafkaConsumer.class);

  @Autowired
  @Qualifier("couchBaseIntegrationImpl")
  private ICouchBaseIntegration couchBaseEgestorImpl;

  @Autowired
  @Qualifier("kafkaLosSyncDataProcessor")
  IKafkaLosSyncDataProcessor kafkaLosSyncDataProcessor;

  @KafkaListener(topics = "WE-LOS-SYNC")
  public void listen(@Payload String message) {
    LOG.info("received message for los sync='{}'", message);

    LosSyncDto losSyncDto = JsonMarshallingUtil.fromString(message, LosSyncDto.class);
    System.out.println(losSyncDto);
    kafkaLosSyncDataProcessor.losSyncDataProcessor(losSyncDto);

  }


}

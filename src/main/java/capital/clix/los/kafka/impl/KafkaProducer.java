package capital.clix.los.kafka.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.query.N1qlQueryRow;
import capital.clix.cache.ConfigUtil;
import capital.clix.distributor.KafkaPushFactory;
import capital.clix.enums.KafkaDistributorType;
import capital.clix.los.bean.ProcessorDto;
import capital.clix.los.couchbaseIntegration.ICouchBaseIntegration;
import capital.clix.los.enums.LoanType;
import capital.clix.los.enums.Property;
import capital.clix.los.kafka.IKafkaProducer;

@Component
public class KafkaProducer implements IKafkaProducer {

  @Autowired
  private KafkaPushFactory kafkaPushFactory;

  @Autowired
  @Qualifier("couchBaseIntegrationImpl")
  private ICouchBaseIntegration couchBaseIntegrationImpl;

  @Override
  public void initiateWorkFlow(ProcessorDto processorDto) {
    N1qlQueryRow row = couchBaseIntegrationImpl.getEntityType(processorDto.getLoanApplicationId());
    JsonObject jsonObject = row.value();
    String type = jsonObject.getString("type");

    if (LoanType.BL.getCode().equalsIgnoreCase(type)) {
      smeInitiateWorkFlow(processorDto);
    }
  }

  private void smeInitiateWorkFlow(ProcessorDto processorDto) {
    kafkaPushFactory.getInstance(KafkaDistributorType.SYNC).distribute(
        ConfigUtil.getPropertyValue(Property.KAFKA_RETRY_TOPIC, String.class), processorDto);
  }
}

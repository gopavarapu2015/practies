package capital.clix.los.kafka;

import capital.clix.los.bean.ProcessorDto;

public interface IKafkaProducer {

  public void initiateWorkFlow(ProcessorDto processorDto);
}

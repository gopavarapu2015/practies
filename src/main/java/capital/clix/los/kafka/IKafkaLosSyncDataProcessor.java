package capital.clix.los.kafka;

import capital.clix.los.bean.LosSyncDto;

public interface IKafkaLosSyncDataProcessor {

  public void losSyncDataProcessor(LosSyncDto losSyncDto);
}

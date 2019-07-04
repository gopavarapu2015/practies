package capital.clix.los.stageupdate;

import capital.clix.los.bean.LosSyncDto;
import capital.clix.los.enums.LosStatus;
import capital.clix.los.enums.WebNotificationDescription;

public interface IStageUpdate {

  public void updateApplicationStage(LosSyncDto losSyncDto, String applicationId,
      LosStatus losStatus);

  public void sendErrorNotification(LosSyncDto losSyncDto,
      WebNotificationDescription webNotificationDescription);

}

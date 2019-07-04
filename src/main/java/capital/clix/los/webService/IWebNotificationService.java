package capital.clix.los.webService;

import capital.clix.los.bean.LosSyncDto;
import capital.clix.los.enums.LosStatus;
import capital.clix.los.enums.WebNotificationDescription;

public interface IWebNotificationService {

  public void sendWebNotification(LosSyncDto losSyncDto, String source, LosStatus losStatus,
      WebNotificationDescription webNotificationDescription);

}

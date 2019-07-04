package capital.clix.los.webService;

import capital.clix.los.bean.LosSyncDto;
import capital.clix.los.enums.NotificationContentCode;

public interface INotificationWebService {

  public void processSmsNotification(LosSyncDto losSyncData,
      NotificationContentCode notificationContentType);

  public void processEmailNotification(LosSyncDto losSyncDto,
      NotificationContentCode notificationContentCode);

}

package capital.clix.los.webService.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import capital.clix.los.enums.PushNotificationsource;
import capital.clix.los.webService.IWebNotificationService;

@Component("pushNotificationFactory")
public class PushNotificationFactory {

  @Autowired
  @Qualifier("clixWebNotificationServiceImpl")
  IWebNotificationService clixWebNotificationServiceImpl;



  public IWebNotificationService getNotificationInstance(String source) {

    if (PushNotificationsource.CLIX.name().equalsIgnoreCase(source)) {
      return clixWebNotificationServiceImpl;
    }

    return null;
  }
}

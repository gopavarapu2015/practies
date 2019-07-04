package capital.clix.los.bean.notification;

import java.util.List;
import java.util.Map;
import capital.clix.los.enums.NotificationContentCode;
import capital.clix.los.enums.NotificationType;

public class SmsNotification {

  private String appId;
  private String applicationId;
  private String from;
  private NotificationType notificationType;
  private Map<String, String> personalizationData;
  private String source;
  private String subAppId;
  private NotificationContentCode contentCode;
  private List<Map> to;

  public String getAppId() {
    return appId;
  }

  public void setAppId(String appId) {
    this.appId = appId;
  }

  public String getApplicationId() {
    return applicationId;
  }

  public void setApplicationId(String applicationId) {
    this.applicationId = applicationId;
  }

  public String getFrom() {
    return from;
  }

  public void setFrom(String from) {
    this.from = from;
  }

  public NotificationType getNotificationType() {
    return notificationType;
  }

  public void setNotificationType(NotificationType notificationType) {
    this.notificationType = notificationType;
  }

  public Map<String, String> getPersonalizationData() {
    return personalizationData;
  }

  public void setPersonalizationData(Map<String, String> personalizationData) {
    this.personalizationData = personalizationData;
  }

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public String getSubAppId() {
    return subAppId;
  }

  public void setSubAppId(String subAppId) {
    this.subAppId = subAppId;
  }

  public NotificationContentCode getContentCode() {
    return contentCode;
  }

  public void setContentCode(NotificationContentCode contentCode) {
    this.contentCode = contentCode;
  }

  public List<Map> getTo() {
    return to;
  }

  public void setTo(List<Map> to) {
    this.to = to;
  }

  @Override
  public String toString() {
    return "SmsNotification [appId=" + appId + ", applicationId=" + applicationId + ", from=" + from
        + ", notificationType=" + notificationType + ", personalizationData=" + personalizationData
        + ", source=" + source + ", subAppId=" + subAppId + ", contentCode=" + contentCode + ", to="
        + to + "]";
  }


}

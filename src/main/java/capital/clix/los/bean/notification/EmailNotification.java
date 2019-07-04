package capital.clix.los.bean.notification;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import capital.clix.los.enums.NotificationContentCode;
import capital.clix.los.enums.NotificationType;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EmailNotification {

  private String applicationId;
  private NotificationType notificationType;
  private Map<String, String> personalizationData;
  private String source;
  private NotificationContentCode contentCode;
  private List<Map<String, String>> to;
  private List<HashMap<String, String>> mailAttachment;

  public String getApplicationId() {
    return applicationId;
  }

  public void setApplicationId(String applicationId) {
    this.applicationId = applicationId;
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

  public NotificationContentCode getContentCode() {
    return contentCode;
  }

  public void setContentCode(NotificationContentCode contentCode) {
    this.contentCode = contentCode;
  }

  public List<Map<String, String>> getTo() {
    return to;
  }

  public void setTo(List<Map<String, String>> to) {
    this.to = to;
  }

  public List<HashMap<String, String>> getMailAttachment() {
    return mailAttachment;
  }

  public void setMailAttachment(List<HashMap<String, String>> mailAttachment) {
    this.mailAttachment = mailAttachment;
  }

  @Override
  public String toString() {
    return "EmailNotification [applicationId=" + applicationId + ", notificationType="
        + notificationType + ", personalizationData=" + personalizationData + ", source=" + source
        + ", contentCode=" + contentCode + ", to=" + to + "]";
  }


}

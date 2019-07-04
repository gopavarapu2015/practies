package capital.clix.los.bean.notification;

import java.util.Map;

public class PushNotification {

  private String loanApplicationId;
  private String userIdCode;
  private String productCode;
  private String source;
  private Map<String, String> notificationData;

  public String getLoanApplicationId() {
    return loanApplicationId;
  }

  public void setLoanApplicationId(String loanApplicationId) {
    this.loanApplicationId = loanApplicationId;
  }

  public String getUserIdCode() {
    return userIdCode;
  }

  public void setUserIdCode(String userIdCode) {
    this.userIdCode = userIdCode;
  }

  public String getProductCode() {
    return productCode;
  }

  public void setProductCode(String productCode) {
    this.productCode = productCode;
  }

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public Map<String, String> getNotificationData() {
    return notificationData;
  }

  public void setNotificationData(Map<String, String> notificationData) {
    this.notificationData = notificationData;
  }

  @Override
  public String toString() {
    return "PushNotification [loanApplicationId=" + loanApplicationId + ", userIdCode=" + userIdCode
        + ", productCode=" + productCode + ", source=" + source + ", notificationData="
        + notificationData + "]";
  }



}

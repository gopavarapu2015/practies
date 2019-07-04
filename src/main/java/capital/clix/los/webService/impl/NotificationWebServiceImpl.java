package capital.clix.los.webService.impl;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.query.N1qlQueryRow;
import capital.clix.cache.ConfigUtil;
import capital.clix.http.HttpClientFactory;
import capital.clix.los.bean.LosSyncDto;
import capital.clix.los.bean.bl.BlLoanEntity;
import capital.clix.los.bean.notification.EmailNotification;
import capital.clix.los.bean.notification.SmsNotification;
import capital.clix.los.couchbaseIntegration.ICouchBaseIntegration;
import capital.clix.los.enums.NotificationContentCode;
import capital.clix.los.enums.NotificationType;
import capital.clix.los.enums.Portfolio;
import capital.clix.los.enums.Property;
import capital.clix.los.webService.INotificationWebService;
import capital.clix.serilization.JsonMarshallingUtil;

@Service("notificationWebServiceImpl")
public class NotificationWebServiceImpl implements INotificationWebService {

  private static final Logger LOG = LogManager.getLogger(NotificationWebServiceImpl.class);
  private final HttpClientFactory httpClientFactory;

  @Autowired
  @Qualifier("couchBaseIntegrationImpl")
  private ICouchBaseIntegration couchBaseIntegrationImpl;

  @Autowired
  NotificationWebServiceImpl(HttpClientFactory httpClientFactory) {
    this.httpClientFactory = httpClientFactory;
  }


  @Override
  public void processSmsNotification(LosSyncDto losSyncData,
      NotificationContentCode notificationContentCode) {

    SmsNotification smsNotificationRequest =
        prepareSmsNotificationBean(losSyncData, notificationContentCode);
    try {
      HttpPost postRequest =
          new HttpPost(ConfigUtil.getPropertyValue(Property.NOTIFICATION_SMS_URL, String.class));
      Header headers = new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json");

      postRequest.setHeader(headers);
      postRequest.setEntity(new StringEntity(JsonMarshallingUtil.toString(smsNotificationRequest),
          StandardCharsets.UTF_8));
      HttpResponse httpResponse = httpClientFactory.getHttpClient().execute(postRequest);
      int responseStatus = httpResponse.getStatusLine().getStatusCode();
      LOG.info("SMS Notification Response received for application id : {} ,statusValue :{}",
          smsNotificationRequest.getApplicationId(), responseStatus);

      if (responseStatus == 200) {
        String responseBody = EntityUtils.toString(httpResponse.getEntity());
        System.out.println("response body:---" + responseBody);


      } else {
        LOG.info("SMS Notification Response received for application id : {} ,statusValue :{}",
            smsNotificationRequest.getApplicationId(), responseStatus);
      }
    } catch (Exception e) {
      LOG.info("Error Received in sending SMS Notification  for application id : {}, data : {} ",
          smsNotificationRequest.getApplicationId(), smsNotificationRequest.toString());
    }
  }

  @Override
  public void processEmailNotification(LosSyncDto losSyncDto,
      NotificationContentCode notificationContentCode) {

    EmailNotification emailNotificationRequest =
        prepareEmailNotificationBean(losSyncDto, notificationContentCode);
    try {
      HttpPost postRequest =
          new HttpPost(ConfigUtil.getPropertyValue(Property.NOTIFICATION_EMAIL_URL, String.class));
      Header headers = new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json");

      postRequest.setHeader(headers);
      postRequest.setEntity(new StringEntity(JsonMarshallingUtil.toString(emailNotificationRequest),
          StandardCharsets.UTF_8));
      LOG.info(" Email Request Prepared is :- "
          + JsonMarshallingUtil.toString(emailNotificationRequest));
      HttpResponse httpResponse = httpClientFactory.getHttpClient().execute(postRequest);
      int responseStatus = httpResponse.getStatusLine().getStatusCode();
      LOG.info("Email Notification Response received for application id : {} ,statusValue :{}",
          emailNotificationRequest.getApplicationId(), responseStatus);

      if (responseStatus == 200) {
        String responseBody = EntityUtils.toString(httpResponse.getEntity());
        LOG.info("email notification response body for application id :{} is :{} ",
            losSyncDto.getApplicationId(), responseBody);


      } else {
        LOG.info("Email Notification Response received for application id : {} ,statusValue :{}",
            emailNotificationRequest.getApplicationId(), responseStatus);
      }
    } catch (Exception e) {
      LOG.info("Error Received in sending Email Notification  for application id : {}, data : {} ",
          emailNotificationRequest.getApplicationId(), emailNotificationRequest.toString());
    }
  }

  private SmsNotification prepareSmsNotificationBean(LosSyncDto losSyncDto,
      NotificationContentCode notificationContentCode) {

    SmsNotification smsNotificationRequest = new SmsNotification();
    smsNotificationRequest
        .setAppId(ConfigUtil.getPropertyValue(Property.NOTIFICATION_APPID, String.class));
    smsNotificationRequest
        .setSubAppId(ConfigUtil.getPropertyValue(Property.NOTIFICATION_SUB_APPID, String.class));

    smsNotificationRequest.setApplicationId(losSyncDto.getApplicationId());
    smsNotificationRequest.setSource("LOS");
    smsNotificationRequest.setNotificationType(NotificationType.SMS);
    smsNotificationRequest
        .setFrom(ConfigUtil.getPropertyValue(Property.NOTIFICATION_FROM, String.class));

    smsNotificationRequest
        .setPersonalizationData((HashMap) losSyncDto.getContent().get("personalization"));

    smsNotificationRequest.setContentCode(notificationContentCode);

    Map<String, String> toMap = getUserInfo(losSyncDto.getApplicationId());
    List<Map> li = new ArrayList<>();
    li.add(toMap);

    smsNotificationRequest.setTo(li);

    return smsNotificationRequest;
  }

  private Map<String, String> getUserInfo(String applicationId) {

    N1qlQueryRow row = couchBaseIntegrationImpl.getEntityType(applicationId);
    JsonObject jsonObject = row.value();
    String type = jsonObject.getString("type");
    Map<String, String> toMap = new HashMap<>();
    if (Portfolio.BL.getCode().equalsIgnoreCase(type)) {
      BlLoanEntity blLoanEntity =
          couchBaseIntegrationImpl.getApplicationEntity(applicationId, BlLoanEntity.class);
      toMap.put("mobile", blLoanEntity.getOwnerMobile());
      toMap.put("emailid", blLoanEntity.getOwnerEmail());
    }
    return toMap;
  }

  private EmailNotification prepareEmailNotificationBean(LosSyncDto losSyncDto,
      NotificationContentCode notificationContentCode) {

    EmailNotification emailNotification = new EmailNotification();
    emailNotification.setApplicationId(losSyncDto.getApplicationId());
    emailNotification.setNotificationType(NotificationType.TRANS);
    emailNotification.setSource("LOS");
    emailNotification.setContentCode(notificationContentCode);


    emailNotification.setPersonalizationData(
        (HashMap<String, String>) losSyncDto.getContent().get("emailDataMap"));

    if (losSyncDto.getContent().get("mailAttachment") != null) {
      List<HashMap<String, String>> mailAttachmentList = new ArrayList();
      mailAttachmentList
          .add((HashMap<String, String>) losSyncDto.getContent().get("mailAttachment"));

      emailNotification.setMailAttachment(mailAttachmentList);

    }

    // Map<String, String> toMap = getUserInfo(losSyncDto.getApplicationId());
    List<Map<String, String>> li = new ArrayList<>();
    Map<String, String> toMap = new HashMap<>();
    toMap.put("emailid", ConfigUtil.getPropertyValue(Property.PB_TEMP_EMAIL, String.class));
    li.add(toMap);

    emailNotification.setTo(li);
    return emailNotification;

  }



}

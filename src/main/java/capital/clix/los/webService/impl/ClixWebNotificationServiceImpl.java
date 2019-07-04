package capital.clix.los.webService.impl;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.HashMap;
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
import capital.clix.http.HttpClientFactory;
import capital.clix.los.bean.LosSyncDto;
import capital.clix.los.bean.bl.BlLoanEntity;
import capital.clix.los.bean.notification.PushNotification;
import capital.clix.los.couchbaseIntegration.ICouchBaseIntegration;
import capital.clix.los.enums.LosStatus;
import capital.clix.los.enums.Portfolio;
import capital.clix.los.enums.WebNotificationDescription;
import capital.clix.los.webService.IWebNotificationService;
import capital.clix.serilization.JsonMarshallingUtil;

@Service("clixWebNotificationServiceImpl")
public class ClixWebNotificationServiceImpl implements IWebNotificationService {

  private static final Logger LOG = LogManager.getLogger(WorkFlowEngineWebServiceImpl.class);
  private final HttpClientFactory httpClientFactory;
  SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
  @Autowired
  @Qualifier("couchBaseIntegrationImpl")
  private ICouchBaseIntegration couchBaseIntegrationImpl;

  @Autowired
  ClixWebNotificationServiceImpl(HttpClientFactory httpClientFactory) {
    this.httpClientFactory = httpClientFactory;
  }

  @Override
  public void sendWebNotification(LosSyncDto losSyncDto, String source, LosStatus losStatus,
      WebNotificationDescription webNotificationDescription) {

    HashMap<WebNotificationDescription, String> notificationMasterData =
        getNotificationMasterData(source);
    String url = notificationMasterData.get(source);
    HttpPost postRequest = new HttpPost(url);
    Header headers = new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json");
    PushNotification notificationRequest = preparePushNotificationRequestBean(losSyncDto,
        notificationMasterData, webNotificationDescription);
    try {

      postRequest.setHeader(headers);
      postRequest.setEntity(new StringEntity(JsonMarshallingUtil.toString(notificationRequest),
          StandardCharsets.UTF_8));

      LOG.info(" Sending notification for application Id:{} with data :{}",
          losSyncDto.getApplicationData(), notificationRequest.toString());

      HttpResponse httpResponse = httpClientFactory.getHttpClient().execute(postRequest);
      int responseStatus = httpResponse.getStatusLine().getStatusCode();


      if (responseStatus == 200) {
        String responseBody = EntityUtils.toString(httpResponse.getEntity());
        LOG.info(
            " Success Response received while sending notification for application Id:{} with data :{} , is:{}",
            losSyncDto.getApplicationData(), notificationRequest.toString(), responseBody);


      } else {
        LOG.error(
            " Error Response received while sending notification for application Id:{} with data :{} , is:{}",
            losSyncDto.getApplicationData(), notificationRequest.toString(), responseStatus);
      }
    } catch (Exception e) {
      LOG.error(" Exception while sending notification for application Id:{} with data :{} is:{}",
          losSyncDto.getApplicationData(), notificationRequest.toString(), e.getMessage());
    }

  }

  private PushNotification preparePushNotificationRequestBean(LosSyncDto losSyncDto,
      HashMap<WebNotificationDescription, String> notificationMasterData,
      WebNotificationDescription webNotificationDescription) {
    LOG.info("WebNotificationDesc:----" + webNotificationDescription);

    StringBuffer desc = new StringBuffer("");
    desc.append(losSyncDto.getApplicationId() + ","
        + notificationMasterData.get(webNotificationDescription.name()));
    if (losSyncDto.getErrorResponseDto() != null) {
      desc.append(" Reason:- ").append(losSyncDto.getErrorResponseDto().getMessage());
    }
    N1qlQueryRow row = couchBaseIntegrationImpl.getEntityType(losSyncDto.getApplicationId());

    JsonObject jsonObject = row.value();
    String type = jsonObject.getString("type");
    PushNotification notificationRequest = new PushNotification();

    if (Portfolio.BL.getCode().equalsIgnoreCase(type)) {
      BlLoanEntity entity = couchBaseIntegrationImpl
          .getApplicationEntity(losSyncDto.getApplicationId(), BlLoanEntity.class);

      notificationRequest.setSource(entity.getSource());
      notificationRequest.setUserIdCode(entity.getOwnerCode());
      notificationRequest.setLoanApplicationId(losSyncDto.getApplicationId());
      notificationRequest.setProductCode(entity.getLoanType());
      Map<String, String> notificationData = new HashMap<>();
      notificationData.put("borrowerName", entity.getRegisteredName());
      notificationData.put("coApplicant", entity.getCoApplicant().get(0).getFirstName());
      notificationData.put("loanAmount", entity.getLoanDetails().getAppliedAmount());
      notificationData.put("tenure", entity.getLoanDetails().getTenure());
      notificationData.put("city", entity.getRegAddress().getCity());
      notificationData.put("stage", entity.getStatus().name());

      notificationData.put("description", desc.toString());
      notificationData.put("dateOfApplication",
          simpleDateFormat.format(entity.getCreated()).toString());

      notificationRequest.setNotificationData(notificationData);


    }
    return notificationRequest;
  }

  private HashMap<WebNotificationDescription, String> getNotificationMasterData(String source) {

    HashMap<WebNotificationDescription, String> notificationMasterDataMap =
        couchBaseIntegrationImpl.getEntityById("PUSH_NOTIFICATION_URL", HashMap.class);

    LOG.info("notificationMasterDataMap:- " + notificationMasterDataMap);
    return notificationMasterDataMap;
  }

}

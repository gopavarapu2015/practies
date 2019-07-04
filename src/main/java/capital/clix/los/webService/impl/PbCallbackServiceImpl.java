package capital.clix.los.webService.impl;

import java.nio.charset.StandardCharsets;
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
import capital.clix.cache.ConfigUtil;
import capital.clix.http.HttpClientFactory;
import capital.clix.los.couchbaseIntegration.ICouchBaseIntegration;
import capital.clix.los.enums.Property;
import capital.clix.los.webService.ICallbackServiceImpl;

@Service("pbCallbackServiceImpl")
public class PbCallbackServiceImpl implements ICallbackServiceImpl {

  private static final Logger LOG = LogManager.getLogger(PbCallbackServiceImpl.class);
  private final HttpClientFactory httpClientFactory;

  @Autowired
  @Qualifier("couchBaseIntegrationImpl")
  private ICouchBaseIntegration couchBaseIntegrationImpl;

  @Autowired
  PbCallbackServiceImpl(HttpClientFactory httpClientFactory) {
    this.httpClientFactory = httpClientFactory;
  }


  @Override
  public void sendCallback(String applicationId, String request) {

    LOG.info(" Sending PB callback request :- {}", request);
    try {
      HttpPost postRequest =
          new HttpPost(ConfigUtil.getPropertyValue(Property.PB_SME_CALLBACK_URL, String.class));
      Header headers = new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json");

      postRequest.setHeader(headers);
      postRequest.setEntity(new StringEntity(request, StandardCharsets.UTF_8));
      HttpResponse httpResponse = httpClientFactory.getHttpClient().execute(postRequest);
      int responseStatus = httpResponse.getStatusLine().getStatusCode();
      LOG.info("Response received for PB Callback application id : {},  data :{},statusValue :{}",
          applicationId, request, responseStatus);

      if (responseStatus == 200) {
        String responseBody = EntityUtils.toString(httpResponse.getEntity());
        System.out.println("response body:---" + responseBody);


      } else {
        LOG.info("Response received for PB Callback application id : {} ,statusValue :{}",
            applicationId, responseStatus);
      }
    } catch (Exception e) {
      LOG.info("Error Received in sending PB callback for application id : {}, exception : {} ",
          applicationId, e.getMessage());
    }

  }

}

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
import org.springframework.stereotype.Service;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import capital.clix.cache.ConfigUtil;
import capital.clix.http.HttpClientFactory;
import capital.clix.los.bean.BaseEntity;
import capital.clix.los.bean.BaseResponse;
import capital.clix.los.bean.WorkFlowRequestBean;
import capital.clix.los.constants.ApplicationConstant;
import capital.clix.los.enums.ErrorState;
import capital.clix.los.enums.Property;
import capital.clix.los.exception.LosException;
import capital.clix.los.webService.IWorkFlowEngineWebService;
import capital.clix.serilization.JsonMarshallingUtil;

@Service("workFlowEngineWebServiceImpl")
public class WorkFlowEngineWebServiceImpl implements IWorkFlowEngineWebService {


  private static final Logger LOG = LogManager.getLogger(WorkFlowEngineWebServiceImpl.class);
  private final HttpClientFactory httpClientFactory;

  @Autowired
  WorkFlowEngineWebServiceImpl(HttpClientFactory httpClientFactory) {
    this.httpClientFactory = httpClientFactory;
  }


  @Override
  @HystrixCommand(fallbackMethod = "fallBackIdenCheck", commandProperties = {
      @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "500")})
  public void MultiBureauCheck(BaseEntity request, BaseResponse response) {

    WorkFlowRequestBean workFlowReq = (WorkFlowRequestBean) request;

    LOG.info("Inside MultiBureauCheck for ApplicationId:{}, WorkFlowRequestBean:{}",
        workFlowReq.getLoanApplicationId(), workFlowReq.toString());

    HttpPost postRequest =
        new HttpPost(ConfigUtil.getPropertyValue(Property.WORKFLOW_URL, String.class));

    Header[] headers = {new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json"),
        new BasicHeader(ApplicationConstant.HEADER_CORELATION, workFlowReq.getUuid())};

    try {
      postRequest.setHeaders(headers);
      postRequest.setEntity(
          new StringEntity(JsonMarshallingUtil.toString(workFlowReq), StandardCharsets.UTF_8));
      HttpResponse httpResponse = httpClientFactory.getHttpClient().execute(postRequest);
      int responseStatus = httpResponse.getStatusLine().getStatusCode();
      response.setResponseStatus(responseStatus);
      LOG.info(
          "Response status recieved for Multi Bureau check with application id : {},corelation_id:{} ,status :{}",
          workFlowReq.getLoanApplicationId(), workFlowReq.getUuid(), responseStatus);

      if (responseStatus == 200) {
        String responseBody = EntityUtils.toString(httpResponse.getEntity());
        LOG.info(
            "Multi Bureau response Body received from WE for ApplicationId:{},corelation_id:{}, is :{}",
            workFlowReq.getLoanApplicationId(), workFlowReq.getUuid(), responseBody);


      } else {
        LOG.error(
            "Response Status Received for MultiBureauCheck for ApplicationId:{},corelation_id:{}, is :{} ",
            workFlowReq.getLoanApplicationId(), workFlowReq.getUuid(), responseStatus);
        throw new LosException(ErrorState.INTERNAL_SERVER_ERROR,
            "Unable to initiate WE with response status :-" + responseStatus);
      }
    } catch (Exception e) {

      LOG.error(
          "Exception Occured while MultiBureauCheck for ApplicationId:{},corelation_id:{}, with Exception:{} ",
          workFlowReq.getLoanApplicationId(), workFlowReq.getUuid(), e.getMessage());
      throw new LosException(ErrorState.INTERNAL_SERVER_ERROR,
          "Unable to initiate WE with exception  :-" + e.getMessage());
    }


  }

  public void fallBackIdenCheck(BaseEntity request, BaseResponse response) {
    LOG.info("fallback called");
    response.setResponse("Fallback");
  }

  @Override
  public void perfiosExcelReportFetch(BaseEntity request, BaseResponse response) {

    WorkFlowRequestBean workFlowReq = (WorkFlowRequestBean) request;
    HttpPost postRequest =
        new HttpPost(ConfigUtil.getPropertyValue(Property.WORKFLOW_URL, String.class));
    Header[] headers = {new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json"),
        new BasicHeader(ApplicationConstant.HEADER_CORELATION, workFlowReq.getUuid())};
    try {
      postRequest.setHeaders(headers);
      postRequest.setEntity(
          new StringEntity(JsonMarshallingUtil.toString(workFlowReq), StandardCharsets.UTF_8));
      HttpResponse httpResponse = httpClientFactory.getHttpClient().execute(postRequest);
      int responseStatus = httpResponse.getStatusLine().getStatusCode();
      LOG.info(
          "Response status recieved for perfios report fetch with data: {},corelation_id:{} ,status :{}",
          workFlowReq.toString(), workFlowReq.getUuid(), responseStatus);

      if (responseStatus == 200) {

      } else {
        LOG.info(
            "Response status recieved for Perfios Excel Fetch with data : {},corelation_id:{} ,status :{}",
            workFlowReq.toString(), workFlowReq.getUuid(), responseStatus);
      }
    } catch (Exception e) {
      LOG.error(
          "Response status recieved for Perfios Excel Fetch with data : {},corelation_id:{} ,exception :{}",
          workFlowReq.toString(), workFlowReq.getUuid(), e);
    }
  }


  @Override
  public void fsaExcelReportFetch(BaseEntity request, BaseResponse response) {

    WorkFlowRequestBean workFlowReq = (WorkFlowRequestBean) request;
    HttpPost postRequest =
        new HttpPost(ConfigUtil.getPropertyValue(Property.WORKFLOW_URL, String.class));
    Header[] headers = {new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json"),
        new BasicHeader(ApplicationConstant.HEADER_CORELATION, workFlowReq.getUuid())};
    try {
      postRequest.setHeaders(headers);
      postRequest.setEntity(
          new StringEntity(JsonMarshallingUtil.toString(workFlowReq), StandardCharsets.UTF_8));
      HttpResponse httpResponse = httpClientFactory.getHttpClient().execute(postRequest);
      int responseStatus = httpResponse.getStatusLine().getStatusCode();
      LOG.info(
          "Response status recieved for FSA report fetch with data: {},corelation_id:{} ,status :{}",
          workFlowReq.toString(), workFlowReq.getUuid(), responseStatus);

      if (responseStatus == 200) {

      } else {
        LOG.info(
            "Response status recieved for FSA Excel Fetch with data : {},corelation_id:{} ,status :{}",
            workFlowReq.toString(), workFlowReq.getUuid(), responseStatus);
      }
    } catch (Exception e) {
      LOG.error(
          "Response status recieved for FSA Excel Fetch with data : {},corelation_id:{} ,exception :{}",
          workFlowReq.toString(), workFlowReq.getUuid(), e);
    }
  }
  
  @Override
  public void SmeStatementAnalysisInitiate(BaseEntity request, BaseResponse response) {

    WorkFlowRequestBean workFlowReq = (WorkFlowRequestBean) request;
    LOG.info(
        "Statement Analysis initiated for application id:--" + workFlowReq.getLoanApplicationId());
    HttpPost postRequest =
        new HttpPost(ConfigUtil.getPropertyValue(Property.WORKFLOW_URL, String.class));
    Header[] headers = {new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json"),
        new BasicHeader(ApplicationConstant.HEADER_CORELATION, workFlowReq.getUuid())};
    try {
      postRequest.setHeaders(headers);
      postRequest.setEntity(
          new StringEntity(JsonMarshallingUtil.toString(workFlowReq), StandardCharsets.UTF_8));
      HttpResponse httpResponse = httpClientFactory.getHttpClient().execute(postRequest);
      int responseStatus = httpResponse.getStatusLine().getStatusCode();
      LOG.info(
          "Response status recieved for Statement Analysis Initiate Request with application id : {},corelation_id:{} ,status :{}",
          request.getId(), workFlowReq.getUuid(), responseStatus);

      if (responseStatus == 200) {

        LOG.info("Statement Analysis initiation completed for application id:{},corelation_id:{}"
            + workFlowReq.getLoanApplicationId(), request.getUuid());
      } else {
        LOG.info(
            "Response status recieved for Statement Analysis Initiate Request with application id : {},corelation_id:{} ,status :{}",
            request.getId(), workFlowReq.getUuid(), responseStatus);
      }
    } catch (Exception e) {
      LOG.info(
          "Exception occured while initiating Statement Analysis via WE with data : {},corelation_id:{} ,exception :{}",
          workFlowReq.toString(), workFlowReq.getUuid(), e);
    }
  }
 @Override
 @HystrixCommand(fallbackMethod = "fallBackIdenCheck", commandProperties = {
	      @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "500")})
  public void FinancialStatementAnalysisInitiate(BaseEntity request, BaseResponse response) {

    WorkFlowRequestBean workFlowReq = (WorkFlowRequestBean) request;
    LOG.info(
        "FSA Analysis initiated for application id:--" + workFlowReq.getLoanApplicationId());
    HttpPost postRequest =
        new HttpPost(ConfigUtil.getPropertyValue(Property.WORKFLOW_URL, String.class));
    Header[] headers = {new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json"),
        new BasicHeader(ApplicationConstant.HEADER_CORELATION, workFlowReq.getUuid())};
    try {
      postRequest.setHeaders(headers);
      postRequest.setEntity(
          new StringEntity(JsonMarshallingUtil.toString(workFlowReq), StandardCharsets.UTF_8));
      HttpResponse httpResponse = httpClientFactory.getHttpClient().execute(postRequest);
      int responseStatus = httpResponse.getStatusLine().getStatusCode();
      LOG.info(
          "Response status recieved for FSA Analysis Initiate Request with application id : {},corelation_id:{} ,status :{}",
          request.getId(), workFlowReq.getUuid(), responseStatus);

      if (responseStatus == 200) {

        LOG.info("FSA Analysis initiation completed for application id:{},corelation_id:{}"
            + workFlowReq.getLoanApplicationId(), request.getUuid());
      } else {
        LOG.info(
            "Response status recieved for FSA Analysis Initiate Request with application id : {},corelation_id:{} ,status :{}",
            request.getId(), workFlowReq.getUuid(), responseStatus);
      }
    } catch (Exception e) {
      LOG.info(
          "Exception occured while initiating FSA Analysis via WE with data : {},corelation_id:{} ,exception :{}",
          workFlowReq.toString(), workFlowReq.getUuid(), e);
    }
  }
@Override
@HystrixCommand(fallbackMethod = "fallBackIdenCheck", commandProperties = {
	      @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "500")})
  public void GstStatementAnalysisInitiate(BaseEntity request, BaseResponse response) {
	  WorkFlowRequestBean workFlowReq = (WorkFlowRequestBean) request;
	    LOG.info(
	        "GST Statement Analysis initiated for application id:--" + workFlowReq.getLoanApplicationId());
	    HttpPost postRequest =
	        new HttpPost(ConfigUtil.getPropertyValue(Property.WORKFLOW_URL, String.class));
	    Header[] headers = {new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json"),
	        new BasicHeader(ApplicationConstant.HEADER_CORELATION, workFlowReq.getUuid())};
	    try {
	      postRequest.setHeaders(headers);
	      postRequest.setEntity(
	          new StringEntity(JsonMarshallingUtil.toString(workFlowReq), StandardCharsets.UTF_8));
	      HttpResponse httpResponse = httpClientFactory.getHttpClient().execute(postRequest);
	      int responseStatus = httpResponse.getStatusLine().getStatusCode();
	      LOG.info(
	          "Response status recieved for GST Statement Analysis Initiate Request with application id : {},corelation_id:{} ,status :{}",
	          request.getId(), workFlowReq.getUuid(), responseStatus);

	      if (responseStatus == 200) {

	        LOG.info("GST Statement Analysis initiation completed for application id:{},corelation_id:{}"
	            + workFlowReq.getLoanApplicationId(), request.getUuid());
	      } else {
	        LOG.info(
	            "Response status recieved for GST Statement Analysis Initiate Request with application id : {},corelation_id:{} ,status :{}",
	            request.getId(), workFlowReq.getUuid(), responseStatus);
	      }
	    } catch (Exception e) {
	      LOG.info(
	          "Exception occured while initiating GST Statement Analysis via WE with data : {},corelation_id:{} ,exception :{}",
	          workFlowReq.toString(), workFlowReq.getUuid(), e);
	    }
  }


  @Override
  public void initiateWorkflowEngine(BaseEntity request, BaseResponse response) {

    WorkFlowRequestBean workFlowReq = (WorkFlowRequestBean) request;
    HttpPost postRequest =
        new HttpPost(ConfigUtil.getPropertyValue(Property.WORKFLOW_URL, String.class));
    Header[] headers = {new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json"),
        new BasicHeader(ApplicationConstant.HEADER_CORELATION, workFlowReq.getUuid())};
    try {
      postRequest.setHeaders(headers);
      postRequest.setEntity(
          new StringEntity(JsonMarshallingUtil.toString(workFlowReq), StandardCharsets.UTF_8));
      HttpResponse httpResponse = httpClientFactory.getHttpClient().execute(postRequest);
      int responseStatus = httpResponse.getStatusLine().getStatusCode();
      LOG.info(
          "Response status recieved for initiating WE with data: {},corelation_id:{} ,status :{}",
          workFlowReq.toString(), workFlowReq.getUuid(), responseStatus);

      if (responseStatus == 200) {

      } else {
        LOG.error(
            "Error Response recieved while initiating WE with data : {},corelation_id:{} ,status :{}",
            workFlowReq.toString(), workFlowReq.getUuid(), responseStatus);
        throw new LosException(ErrorState.INTERNAL_SERVER_ERROR, "Unable to initiate WE");
      }
    } catch (Exception e) {
      LOG.error(
          "Response status recieved while initiating WE with data : {},corelation_id:{} ,exception :{}",
          workFlowReq.toString(), workFlowReq.getUuid(), e);
      throw new LosException(ErrorState.INTERNAL_SERVER_ERROR, "Unable to initiate WE");
    }

  }

}

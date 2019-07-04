package capital.clix.los.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.query.N1qlQueryRow;
import capital.clix.los.bean.ApplicationClose;
import capital.clix.los.bean.BaseResponse;
import capital.clix.los.bean.ErrorResponseDto;
import capital.clix.los.bean.PersonalLoanEntity;
import capital.clix.los.bean.bl.ApplicationSearch;
import capital.clix.los.bean.bl.BlLoanEntity;
import capital.clix.los.bean.bl.SmePerfiosReport;
import capital.clix.los.bean.cibil.ReportFetchRequest;
import capital.clix.los.bean.laep.LAEPLoanEntity;
import capital.clix.los.bean.pb.PBLoanEntity;
import capital.clix.los.couchbaseIntegration.ICouchBaseIntegration;
import capital.clix.los.enums.ApplicationStage;
import capital.clix.los.enums.LoanType;
import capital.clix.los.enums.Portfolio;
import capital.clix.los.factory.ReportFetchInstance;
import capital.clix.los.service.IAuditLoggingService;
import capital.clix.los.service.IDataService;
import capital.clix.los.service.IReportFetchService;

@Service("dataServiceImpl")
public class DataServiceImpl implements IDataService {

  @Autowired
  @Qualifier("couchBaseIntegrationImpl")
  private ICouchBaseIntegration couchBaseEgestorImpl;

  @Autowired
  private ReportFetchInstance reportFetchInstance;

  @Autowired
  private IAuditLoggingService auditLoggingServiceImpl;

  private static final Logger LOG = LogManager.getLogger(DataServiceImpl.class);

  @Override
  public List<Map> applicationSearch(ApplicationSearch applicationSearch) {

    List<Map> finalDataList = new ArrayList();
    try {
      LOG.info("Processing Search request for application with request:{},corelation_id :{}",
          applicationSearch.toString(), applicationSearch.getUuid());
      List<N1qlQueryRow> rowList = couchBaseEgestorImpl.searchBLApplication(applicationSearch);
      Map<String, Object> dataMap = null;
      for (N1qlQueryRow row : rowList) {

        dataMap = new HashMap<>();

        JsonObject obj = row.value();
        dataMap.put("id",
            obj.get("_ID").toString().contains(":") ? obj.get("_ID").toString().split(":")[1]
                : obj.get("_ID").toString());
        dataMap.put("status", obj.get("status"));

        dataMap.put("borrowerName", obj.get("borrowerName"));
        dataMap.put("coApplicant", obj.get("coApplicant"));
        dataMap.put("city", obj.get("city"));
        dataMap.put("loanAmount", obj.get("loanAmount"));
        dataMap.put("loanPeriod", obj.get("loanPeriod"));
        if (obj.get("createdDate") != null
            && StringUtils.isNumeric(obj.get("createdDate").toString())) {
          dataMap.put("createdDate", new Date(Long.valueOf(obj.get("createdDate").toString())));
        }


        finalDataList.add(dataMap);


      }
      LOG.info(
          "Processing complete for Search request for application with request:{},corelation_id :{}",
          applicationSearch.toString(), applicationSearch.getUuid());
    } catch (Exception e) {
      LOG.error(
          "Exception Occured while Searching for application with request:{},corelation_id :{},exception:{}",
          applicationSearch.toString(), applicationSearch.getUuid(), e);
    }
    return finalDataList;
  }

  @Override
  public ApplicationStage fetchStatusById(String id) {

    ApplicationStage status = null;
    try {
      LOG.info("Fetching Status for applicationId:{}", id);
      N1qlQueryRow row = couchBaseEgestorImpl.getEntityType(id);
      JsonObject jsonObject = row.value();
      String type = jsonObject.getString("type");
      if (Portfolio.BL.getCode().equalsIgnoreCase(type)) {
        BlLoanEntity request = couchBaseEgestorImpl.getApplicationEntity(id, BlLoanEntity.class);
        status = request.getStatus();
      } else if (Portfolio.PL.getCode().equalsIgnoreCase(type)) {

        PersonalLoanEntity request =
            couchBaseEgestorImpl.getApplicationEntity(id, PersonalLoanEntity.class);
        status = request.getStatus();
      } else if (LoanType.SME_PB.name().equalsIgnoreCase(type)) {

        PBLoanEntity request = couchBaseEgestorImpl.getApplicationEntity(id, PBLoanEntity.class);
        status = request.getStatus();
      }else if (LoanType.LAEP.name().equalsIgnoreCase(type)) {

          LAEPLoanEntity request = couchBaseEgestorImpl.getApplicationEntity(id, LAEPLoanEntity.class);
          status = request.getStatus();
        }
    } catch (Exception e) {
      LOG.error("Exception Occured while Fetching status for applicationId:{}, Exception:{}", id,
          e);
    }
    return status;
  }

  @Override
  public BaseResponse closeLoan(ApplicationClose applicationClose, BaseResponse response) {

    N1qlQueryRow row = couchBaseEgestorImpl.getEntityType(applicationClose.getLoanApplicationId());
    JsonObject jsonObject = row.value();
    String type = jsonObject.getString("type");

    String status = "Application Closed Successfully";
    LOG.info("closing application with data:{},corelation_id:{},type:{}",
        applicationClose.toString(), applicationClose.getUuid(), type);
    try {
      if (Portfolio.BL.getCode().equalsIgnoreCase(type)) {
        BlLoanEntity blLoanEntity = couchBaseEgestorImpl
            .getApplicationEntity(applicationClose.getLoanApplicationId(), BlLoanEntity.class);

        if (blLoanEntity != null) {
          blLoanEntity.setStatus(ApplicationStage.CLOSED);
          blLoanEntity.setApplicationClose(applicationClose);
          couchBaseEgestorImpl.updateLoanEntity(blLoanEntity);
          response.setSuccess(true);
          response.setResponse(status);
        } else {
          response.setSuccess(false);
          response.setResponse("Unable To Close the application");
        }
      }
      LOG.info("Application Closed with data:{},corelation_id:{}", applicationClose.toString(),
          applicationClose.getUuid());

    } catch (Exception e) {

      LOG.error(
          "Exception Occured while Closing Application with data:{},corelation_id:{},error:{}",
          applicationClose.toString(), applicationClose.getUuid(), e);
      response.setSuccess(false);
      response.setResponse("Unable To Close the application");
    }

    return response;
  }

  @Override
  public List getReports(ReportFetchRequest reportFetchRequest) {

    N1qlQueryRow row = couchBaseEgestorImpl.getEntityType(reportFetchRequest.getApplicationId());
    JsonObject jsonObject = row.value();
    String type = jsonObject.getString("type");

    IReportFetchService reportFetchService =
        reportFetchInstance.getReportFetchServiceInstanceBasedOnType(type);

    if (reportFetchService != null) {
      return reportFetchService.fetchReports(reportFetchRequest);
    }

    return null;
  }

  @Override
  public SmePerfiosReport fetchSmePerfiosReport(String id) {

    SmePerfiosReport smePerfiosReport = null;
    try {
      LOG.info("Fetching Perfios Report for id:{}", id);

      smePerfiosReport = couchBaseEgestorImpl.getEntity(id, SmePerfiosReport.class);
      smePerfiosReport.setSuccess(true);
      LOG.info("Perfios Report fetched for id:{},is:{}", id, smePerfiosReport.toString());
    } catch (Exception e) {
      smePerfiosReport = new SmePerfiosReport();
      ErrorResponseDto errorResponseDto = new ErrorResponseDto();
      errorResponseDto.setMessage("Error While Fetching Perfios Report");
      errorResponseDto.setError(e.getMessage());
      smePerfiosReport.setErrorResponseDto(errorResponseDto);
      smePerfiosReport.setSuccess(false);
      LOG.error("Error While Fetching Perfios Report id:{},is:{}", id, e);
    }
    return smePerfiosReport;
  }


}

package capital.clix.los.egestor.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.query.N1qlQueryRow;
import com.google.gson.internal.LinkedTreeMap;
import capital.clix.los.bean.BaseEntity;
import capital.clix.los.bean.BaseResponse;
import capital.clix.los.bean.WorkFlowRequestBean;
import capital.clix.los.bean.bl.BlLoanEntity;
import capital.clix.los.bean.bl.SmePerfiosReport;
import capital.clix.los.couchbaseIntegration.ICouchBaseIntegration;
import capital.clix.los.egestor.IEgestor;
import capital.clix.los.enums.ApplicationStage;
import capital.clix.los.enums.AuditDataKey;
import capital.clix.los.enums.LoanType;
import capital.clix.los.webService.IWorkFlowEngineWebService;

@Service("perfiosExcelReportFetchEgestorImpl")
public class PerfiosExcelReportFetchEgestorImpl implements IEgestor {

  @Autowired
  @Qualifier("workFlowEngineWebServiceImpl")
  private IWorkFlowEngineWebService workFlowEngineWebServiceImpl;

  @Autowired
  @Qualifier("couchBaseIntegrationImpl")
  private ICouchBaseIntegration couchBaseEgestorImpl;

  private static final Logger LOG = LogManager.getLogger(PerfiosExcelReportFetchEgestorImpl.class);

  @Override
  public void preProcess(BaseEntity request, BaseResponse response) {
    Map<AuditDataKey, String> auditData = new HashMap<>();
    SmePerfiosReport perfiosCallback = (SmePerfiosReport) request;
    LOG.info("Inside PreProcess for Perfios Excel Report Fetch with ApplicationId:{}",
        perfiosCallback.getLoanApplicationId());
    auditData.put(AuditDataKey.APPLICATION_ID, perfiosCallback.getLoanApplicationId());
    auditData.put(AuditDataKey.STAGE, "Perfios Excel Report Fetch Pre Process");
    auditData.put(AuditDataKey.TX_UUID, perfiosCallback.getUuid());
    couchBaseEgestorImpl.saveAuditTrailEntity(auditData);
  }

  @Override
  public void process(BaseEntity request, BaseResponse response) {

    SmePerfiosReport perfiosCallback = (SmePerfiosReport) request;
    try {
      WorkFlowRequestBean workFlowRequestBean = new WorkFlowRequestBean();
      workFlowRequestBean.setLoanApplicationId(perfiosCallback.getLoanApplicationId());
      workFlowRequestBean.setStage(ApplicationStage.STATEMENT_ANALYSIS_CALLBACK.getValue());
      workFlowRequestBean.setUuid(perfiosCallback.getUuid());
      LinkedTreeMap transientData = new LinkedTreeMap<>();

      transientData.put("applicantCode",
          perfiosCallback.getApplicantReports().get("applicantCode"));
      setAdditionalDetailsForPerfiosDelphi(perfiosCallback.getLoanApplicationId(), transientData);

      workFlowRequestBean.setTransientData(transientData);
      LOG.info(
          "Inside Process for Perfios Excel Report Fetch with ApplicationId:{},Calling WE with request:{},corelation_id:{}",
          perfiosCallback.getLoanApplicationId(), workFlowRequestBean.toString(),
          perfiosCallback.getUuid());
      N1qlQueryRow row = couchBaseEgestorImpl.getEntityType(perfiosCallback.getLoanApplicationId());
      JsonObject jsonObject = row.value();
      String type = jsonObject.getString("type");
      if ("BL".equalsIgnoreCase(type)) {
        workFlowRequestBean.setLoanType(LoanType.BL);
      }
      workFlowEngineWebServiceImpl.perfiosExcelReportFetch(workFlowRequestBean, response);
      LOG.info(
          "Inside Process for Perfios Excel Report Fetch with ApplicationId:{},WE request sent with corelation_id:{}",
          perfiosCallback.getLoanApplicationId(), perfiosCallback.getUuid());
    } catch (Exception e) {

      LOG.error(
          "Exception Occured while Perfios Excel Report Fetch with ApplicationId:{},corelation_id:{},Exception is:{}",
          perfiosCallback.getLoanApplicationId(), perfiosCallback.getUuid(), e);
      response.setResponse("Error While Fetching Perfios Excel Report");
    }

  }

  @Override
  public void postProcess(BaseEntity request, BaseResponse response) {
    Map<AuditDataKey, String> auditData = new HashMap<>();
    SmePerfiosReport perfiosCallback = (SmePerfiosReport) request;
    LOG.info("Inside Post Process for Perfios Excel Report Fetch with ApplicationId:{}",
        perfiosCallback.getLoanApplicationId());
    auditData.put(AuditDataKey.APPLICATION_ID, perfiosCallback.getLoanApplicationId());
    auditData.put(AuditDataKey.STAGE, "Perfios Excel Report Fetch Post Process");
    auditData.put(AuditDataKey.ERROR, response.getResponse());
    auditData.put(AuditDataKey.TX_UUID, perfiosCallback.getUuid());
    couchBaseEgestorImpl.saveAuditTrailEntity(auditData);
  }

  private void setAdditionalDetailsForPerfiosDelphi(String loanApplicationId,
      LinkedTreeMap transientData) {

    BlLoanEntity blLoanEntity =
        couchBaseEgestorImpl.getApplicationEntity(loanApplicationId, BlLoanEntity.class);

    String applicantCode = transientData.get("applicantCode").toString();
    if (blLoanEntity != null) {
      List<Map<String, Object>> bankDataList =
          (ArrayList<Map<String, Object>>) blLoanEntity.getDocument().get("BANK_STATEMENT");

      for (Map<String, Object> map : bankDataList) {
        if (map.get("applicantCode") != null
            && map.get("applicantCode").toString().equalsIgnoreCase(applicantCode)) {
          transientData.put("drawingPower", map.get("drawingPower"));
          transientData.put("accountType", map.get("accountType"));
          transientData.put("sanctionLimit", map.get("sanctionLimit"));
          break;

        }
      }

    }

  }

}

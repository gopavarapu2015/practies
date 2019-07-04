package capital.clix.los.egestor.impl;

import java.util.HashMap;
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
import capital.clix.los.bean.LosSyncDto;
import capital.clix.los.bean.bl.BlLoanEntity;
import capital.clix.los.bean.bl.SmePerfiosReport;
import capital.clix.los.couchbaseIntegration.ICouchBaseIntegration;
import capital.clix.los.egestor.IEgestor;
import capital.clix.los.enums.ApplicationStage;
import capital.clix.los.enums.AuditDataKey;
import capital.clix.los.enums.ErrorState;
import capital.clix.los.enums.Prefix;
import capital.clix.los.enums.WebNotificationDescription;
import capital.clix.los.exception.LosException;
import capital.clix.los.webService.IWebNotificationService;

@Service("perfiosCallbackEgestorImpl")
public class PerfiosCallbackEgestorImpl implements IEgestor {

  @Autowired
  @Qualifier("couchBaseIntegrationImpl")
  private ICouchBaseIntegration couchBaseIntegrationImpl;

  @Autowired
  @Qualifier("clixWebNotificationServiceImpl")
  private IWebNotificationService clixWebNotificationServiceImpl;

  private static final Logger LOG = LogManager.getLogger(PerfiosCallbackEgestorImpl.class);

  @Override
  public void preProcess(BaseEntity request, BaseResponse response) {

    SmePerfiosReport smePerfiosReport = (SmePerfiosReport) request;
    LOG.info("Inside PreProcess for Perfios Callback with ApplicationId:{}", request.getId());
    Map<AuditDataKey, String> auditData = new HashMap<>();
    auditData.put(AuditDataKey.APPLICATION_ID, smePerfiosReport.getLoanApplicationId());
    auditData.put(AuditDataKey.STAGE, "Perfios Callback Pre Process");
    auditData.put(AuditDataKey.TX_UUID, request.getUuid());
    couchBaseIntegrationImpl.saveAuditTrailEntity(auditData);
  }

  @Override
  public void process(BaseEntity request, BaseResponse response) {

    SmePerfiosReport smePerfiosReport = (SmePerfiosReport) request;
    if (!smePerfiosReport.getSuccess()) {

      LOG.info(" Sending Perfios Error Notification for application id :-"
          + smePerfiosReport.getLoanApplicationId());
      sendPerfiosAnalysisErrorNotification(smePerfiosReport);
      updateApplicationStage(smePerfiosReport.getLoanApplicationId(),
          ApplicationStage.BANK_ANALYSIS_FAILED);
      throw new LosException(ErrorState.INTERNAL_SERVER_ERROR,
          "Perfios Analysis Failed and Received report is Blank with status false");
    }

    try {

      LOG.info("Inside Process for Saving Perfios Report for corelation_id:{},data:{}",
          smePerfiosReport.getUuid(), smePerfiosReport.toString());
      N1qlQueryRow row =
          couchBaseIntegrationImpl.getEntityType(smePerfiosReport.getLoanApplicationId());

      JsonObject obj = row.value();

      String type = obj.get("type").toString();

      LinkedTreeMap report = smePerfiosReport.getApplicantReports();

      System.out.println(Prefix.PERFIOS.getCode() + smePerfiosReport.getLoanApplicationId() + "-"
          + report.get("applicantCode"));

      SmePerfiosReport smePerfiosReport1 = couchBaseIntegrationImpl
          .getEntityById(Prefix.PERFIOS.getCode() + smePerfiosReport.getLoanApplicationId() + "-"
              + report.get("applicantCode"), SmePerfiosReport.class);
      if (smePerfiosReport1 != null) {

        smePerfiosReport1.setApplicantReports(report);
        couchBaseIntegrationImpl.updateLoanEntity(smePerfiosReport1);

        LOG.info(
            "Perfios Report saved successfully and stage updated for application id{},corelation_id:{}",
            smePerfiosReport.getLoanApplicationId(), smePerfiosReport.getUuid());
      }
      response.setResponse("Perfios Called Successfully");
      response.setSuccess(true);

    } catch (Exception e) {
      LOG.error(" Exception occured while saving perfios report for application id: {} is :{}",
          smePerfiosReport.getLoanApplicationId(), e);
      throw new LosException(ErrorState.INTERNAL_SERVER_ERROR,
          "Exception Occured while saving perfios report");

    }
  }

  private void sendPerfiosAnalysisErrorNotification(SmePerfiosReport smePerfiosReport) {
    LosSyncDto losSyncDto = new LosSyncDto();
    N1qlQueryRow row = couchBaseIntegrationImpl.getSingleField(
        Prefix.APPLICATION.getCode() + smePerfiosReport.getLoanApplicationId(), "source");
    JsonObject jsonObject = row.value();
    String source = jsonObject.getString("source");


    losSyncDto.setApplicationId(smePerfiosReport.getLoanApplicationId());
    clixWebNotificationServiceImpl.sendWebNotification(losSyncDto, source, null,
        WebNotificationDescription.SME_BANK_ANALYSIS_FAIL);
  }

  @Override
  public void postProcess(BaseEntity request, BaseResponse response) {

    SmePerfiosReport smePerfiosReport = (SmePerfiosReport) request;
    LOG.info("Inside Post Process for Perfios Callback with ApplicationId:{}", request.getId());
    Map<AuditDataKey, String> auditData = new HashMap<>();
    auditData.put(AuditDataKey.APPLICATION_ID, smePerfiosReport.getLoanApplicationId());
    auditData.put(AuditDataKey.STAGE, "Perfios Callback Post Process");
    auditData.put(AuditDataKey.TX_UUID, request.getUuid());
    auditData.put(AuditDataKey.ERROR, response.getResponse());
    couchBaseIntegrationImpl.saveAuditTrailEntity(auditData);
  }

  private void updateApplicationStage(String loanApplicationId, ApplicationStage applicationStage) {

    BlLoanEntity blLoanEntity =
        couchBaseIntegrationImpl.getApplicationEntity(loanApplicationId, BlLoanEntity.class);

    blLoanEntity.setStatus(applicationStage);

    couchBaseIntegrationImpl.updateLoanEntity(blLoanEntity);

  }

}

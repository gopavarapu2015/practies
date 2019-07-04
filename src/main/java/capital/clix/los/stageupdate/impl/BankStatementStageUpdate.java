package capital.clix.los.stageupdate.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.couchbase.core.CouchbaseTemplate;
import org.springframework.stereotype.Service;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.query.N1qlQueryRow;
import capital.clix.los.bean.LosSyncDto;
import capital.clix.los.bean.bl.BlLoanEntity;
import capital.clix.los.bean.bl.SmePerfiosDelphiReport;
import capital.clix.los.bean.bl.SmePerfiosReport;
import capital.clix.los.constants.ApplicationConstant;
import capital.clix.los.couchbaseIntegration.ICouchBaseIntegration;
import capital.clix.los.enums.ApplicationStage;
import capital.clix.los.enums.LosStatus;
import capital.clix.los.enums.Portfolio;
import capital.clix.los.enums.Prefix;
import capital.clix.los.enums.WebNotificationDescription;
import capital.clix.los.stageupdate.IStageUpdate;
import capital.clix.los.webService.INotificationWebService;
import capital.clix.los.webService.IWebNotificationService;
import capital.clix.los.webService.impl.PushNotificationFactory;
import capital.clix.util.JsonMarshallingUtil;

@Service("bankStatementStageUpdate")
public class BankStatementStageUpdate implements IStageUpdate {

  @Autowired
  @Qualifier("couchBaseIntegrationImpl")
  private ICouchBaseIntegration couchBaseIntegrationImpl;

  @Autowired
  private CouchbaseTemplate template;

  @Autowired
  @Qualifier("notificationWebServiceImpl")
  INotificationWebService notificationWebServiceImpl;

  @Autowired
  @Qualifier("pushNotificationFactory")
  PushNotificationFactory pushNotificationFactory;

  private static final Logger LOG = LogManager.getLogger(BankStatementStageUpdate.class);

  @Override
  public void updateApplicationStage(LosSyncDto losSyncDto, String applicationId,
      LosStatus losStatus) {

    if (losSyncDto.getErrorResponseDto() != null) {

      LOG.info(
          " Sending Statement Analysis Error notification for application Id :{}, and stage Data : {}",
          applicationId, losSyncDto.toString());
      if (losSyncDto.getStage()
          .equalsIgnoreCase(ApplicationStage.STATEMENT_ANALYSIS_INITIATE.getValue())) {

        LOG.info("Updating the stage :{},for application id:{}",
            ApplicationStage.STATEMENT_INITIATION_FAILED, losSyncDto.getApplicationId());

        updateApplicationStatus(Prefix.APPLICATION.getCode() + losSyncDto.getApplicationId(),
            ApplicationStage.STATEMENT_INITIATION_FAILED);

        sendWebNotification(losSyncDto, losStatus,
            WebNotificationDescription.SME_BANK_ANALYSIS_INITIATE_FAIL);
      } else if (losSyncDto.getStage()
          .equalsIgnoreCase(ApplicationStage.STATEMENT_ANALYSIS_CALLBACK.getValue())) {

        LOG.info("Updating the stage :{},for application id:{}",
            ApplicationStage.BANK_ANALYSIS_FAILED, losSyncDto.getApplicationId());

        updateApplicationStatus(Prefix.APPLICATION.getCode() + losSyncDto.getApplicationId(),
            ApplicationStage.BANK_ANALYSIS_FAILED);


        sendWebNotification(losSyncDto, losStatus,
            WebNotificationDescription.SME_BANK_ANALYSIS_FAIL);
      }
      return;
    }

    LOG.info(" Updating the stage for application Id :{}, with Data:{}", applicationId,
        losSyncDto.toString());

    switch (losStatus) {
      case STATEMENT_ANALYSIS_INITIATED:

        LOG.info("Updating the stage :{},for application id:{}",
            ApplicationStage.STATEMENT_ANALYSIS_INITIATE, losSyncDto.getApplicationId());
        processStatementInitiationResponse(losSyncDto);
        updateApplicationStatus(Prefix.APPLICATION.getCode() + losSyncDto.getApplicationId(),
            ApplicationStage.STATEMENT_ANALYSIS_INITIATE);
        sendWebNotification(losSyncDto, losStatus,
            WebNotificationDescription.SME_BANK_ANALYSIS_INITIATE_SUCCESSFULL);
        break;

      case STATEMENT_ANALYSIS_COMPLETE:

        processSmePerfiosDelphiData(losSyncDto);
        updateSmePerfiosExcelReport(losSyncDto);
        sendWebNotification(losSyncDto, losStatus,
            WebNotificationDescription.SME_BANK_ANALYSIS_SUCCESSFULL);

        break;

      case SME_PERFIOS_DELPHI_COMPLETE:

        LOG.info("Updating the stage :{},for application id:{}", "SME_PERFIOS_DELPHI_COMPLETE",
            losSyncDto.getApplicationId());

        // updateSmePerfiosExcelReport(losSyncDto);
        processSmePerfiosDelphiData(losSyncDto);

        break;
    }

  }

  private void processStatementInitiationResponse(LosSyncDto losSyncDto) {

    if (losSyncDto.getContent().containsKey("saInitiateResponse")) {

      List<Map<String, String>> saInitiateDataMapList =
          (ArrayList) losSyncDto.getContent().get("saInitiateResponse");
      Map<String, String> dataMap = null;
      if (saInitiateDataMapList != null && !saInitiateDataMapList.isEmpty()) {

        Iterator itr = saInitiateDataMapList.iterator();
        HashMap<String, String> finalApplicantCodeMap = new HashMap<>(saInitiateDataMapList.size());
        while (itr.hasNext()) {
          dataMap = (HashMap) itr.next();

          HashMap<String, String> applicantCodeMap =
              JsonMarshallingUtil.fromString(dataMap.get("applicantCodeMap"), HashMap.class);
          finalApplicantCodeMap.putAll(applicantCodeMap);
        }
        N1qlQueryRow row = couchBaseIntegrationImpl.getEntityType(losSyncDto.getApplicationId());
        JsonObject jsonObject = row.value();
        String type = jsonObject.getString("type");
        if (Portfolio.BL.getCode().equalsIgnoreCase(type)) {
          BlLoanEntity blLoanEntity = couchBaseIntegrationImpl
              .getApplicationEntity(losSyncDto.getApplicationId(), BlLoanEntity.class);

          List<Map<String, Object>> bankDataList =
              (ArrayList<Map<String, Object>>) blLoanEntity.getDocument().get("BANK_STATEMENT");

          for (Map<String, Object> map : bankDataList) {

            String key = blLoanEntity.getRegisteredName() + ApplicationConstant.SEPARATOR
                + blLoanEntity.getPan() + ApplicationConstant.SEPARATOR + map.get("correlationId");

            String applicantCode = finalApplicantCodeMap.get(key);

            map.put("applicantCode", applicantCode);

            SmePerfiosReport smePerfiosReport = template.findById(
                Prefix.PERFIOS.getCode() + losSyncDto.getApplicationId() + "-" + applicantCode,
                SmePerfiosReport.class);
            if (smePerfiosReport == null) {
              smePerfiosReport = new SmePerfiosReport();
              smePerfiosReport.setAccountHolderName(
                  map.get("entityName") != null ? map.get("entityName").toString() : null);

              smePerfiosReport.setSanctionLimit(
                  map.get("sanctionLimit") != null ? map.get("sanctionLimit").toString() : null);

              smePerfiosReport.setAccountType(
                  map.get("account") != null ? map.get("account").toString() : null);

              smePerfiosReport.setId(
                  Prefix.PERFIOS.getCode() + losSyncDto.getApplicationId() + "-" + applicantCode);
              smePerfiosReport.setLoanApplicationId(losSyncDto.getApplicationId());
              smePerfiosReport.setYearMonthTo(
                  map.get("yearMonthTo") != null ? map.get("yearMonthTo").toString() : null);
              smePerfiosReport.setYearMonthFrom(
                  map.get("yearMonthFrom") != null ? map.get("yearMonthFrom").toString() : null);
              template.save(smePerfiosReport);

            }
          }
          template.update(blLoanEntity);
        }
      }
    }
  }

  private void updateApplicationStatus(String applicationId, ApplicationStage stage) {
    if (applicationId.contains(":")) {
      applicationId = applicationId.split(":")[1];
    }
    N1qlQueryRow row = couchBaseIntegrationImpl.getEntityType(applicationId);
    JsonObject jsonObject = row.value();
    String type = jsonObject.getString("type");
    if (Portfolio.BL.getCode().equalsIgnoreCase(type)) {
      BlLoanEntity blLoanEntity =
          couchBaseIntegrationImpl.getApplicationEntity(applicationId, BlLoanEntity.class);
      blLoanEntity.setStatus(stage);
      couchBaseIntegrationImpl.updateLoanEntity(blLoanEntity);
    }

  }

  private void updateSmePerfiosExcelReport(LosSyncDto losSyncDto) {
    if (losSyncDto.getContent().containsKey("StatementExcelReport")) {

      Map<String, Map<String, Object>> applicantCodeDocDataMap =
          (Map<String, Map<String, Object>>) losSyncDto.getContent().get("StatementExcelReport");

      if (applicantCodeDocDataMap != null && !applicantCodeDocDataMap.isEmpty()) {

        for (Map.Entry<String, Map<String, Object>> entry : applicantCodeDocDataMap.entrySet()) {

          SmePerfiosReport smePerfiosReport =
              template.findById(Prefix.PERFIOS.getCode() + losSyncDto.getApplicationId()
                  + ApplicationConstant.SEPARATOR + entry.getKey(), SmePerfiosReport.class);

          if (smePerfiosReport != null) {
            smePerfiosReport.setPerfiosExcelReportData(entry.getValue());
            template.update(smePerfiosReport);
          }
        }
      }


    }
    LOG.info("Updating the stage :{},for application id:{}",
        ApplicationStage.STATEMENT_ANALYSIS_COMPLETE, losSyncDto.getApplicationId());

    updateApplicationStatus(Prefix.APPLICATION.getCode() + losSyncDto.getApplicationId(),
        ApplicationStage.STATEMENT_ANALYSIS_COMPLETE);
  }

  private void processSmePerfiosDelphiData(LosSyncDto losSyncDto) {

    if (losSyncDto.getContent().containsKey("smeDelphiReport")) {
      Map<String, Object> smePerfiosDataMap =
          (HashMap<String, Object>) losSyncDto.getContent().get("smeDelphiReport");

      String applicantCode = smePerfiosDataMap.get("applicantCode").toString();

      N1qlQueryRow row = couchBaseIntegrationImpl.getEntityType(losSyncDto.getApplicationId());
      JsonObject jsonObject = row.value();
      String type = jsonObject.getString("type");
      if (Portfolio.BL.getCode().equalsIgnoreCase(type)) {
        BlLoanEntity blLoanEntity = couchBaseIntegrationImpl
            .getApplicationEntity(losSyncDto.getApplicationId(), BlLoanEntity.class);

        List<Map<String, Object>> bankDataList =
            (ArrayList<Map<String, Object>>) blLoanEntity.getDocument().get("BANK_STATEMENT");

        for (Map<String, Object> map : bankDataList) {

          if (applicantCode.equalsIgnoreCase(map.get("applicantCode").toString())) {
            map.put("delphiComplete", "Y");
          }
        }
        couchBaseIntegrationImpl.updateLoanEntity(blLoanEntity);
      }

      SmePerfiosReport smePerfiosReport =
          template.findById(Prefix.PERFIOS.getCode() + losSyncDto.getApplicationId()
              + ApplicationConstant.SEPARATOR + applicantCode, SmePerfiosReport.class);

      if (smePerfiosReport != null) {
        if (smePerfiosDataMap != null && !smePerfiosDataMap.isEmpty()) {
          SmePerfiosDelphiReport smePerfiosDelphiReport = new SmePerfiosDelphiReport();
          smePerfiosDelphiReport.setLoanApplicationId(losSyncDto.getApplicationId());
          if (smePerfiosDataMap.get("reportType") != null) {
            smePerfiosDelphiReport.setReportType(smePerfiosDataMap.get("reportType").toString());
          }
          smePerfiosDelphiReport.setRequestId(smePerfiosDataMap.get("requestId").toString());
          smePerfiosDelphiReport.setSource(smePerfiosDataMap.get("source").toString());
          smePerfiosDelphiReport.setPerfiodDelphiReport(
              (LinkedHashMap<String, String>) smePerfiosDataMap.get("perfiodDelphiReport"));

          smePerfiosReport.setSmePerfiosDelphiReport(smePerfiosDelphiReport);
          template.update(smePerfiosReport);
        }
      }
      LOG.info("Perfios Delphi Saved for application id:{} and applicantCode:{}",
          losSyncDto.getApplicationId(), applicantCode);
    }
  }

  @Override
  public void sendErrorNotification(LosSyncDto losSyncDto,
      WebNotificationDescription webNotificationDescription) {

  }


  private void sendWebNotification(LosSyncDto losSyncDto, LosStatus losStatus,
      WebNotificationDescription webNotificationDescription) {

    N1qlQueryRow row = couchBaseIntegrationImpl
        .getSingleField(Prefix.APPLICATION.getCode() + losSyncDto.getApplicationId(), "source");
    JsonObject jsonObject = row.value();
    String source = jsonObject.getString("source");
    IWebNotificationService webNotificationServiceImpl =
        pushNotificationFactory.getNotificationInstance(source);
    webNotificationServiceImpl.sendWebNotification(losSyncDto, source, losStatus,
        webNotificationDescription);

  }

  // private void sendSMSEmailNotification(LosSyncDto losSyncDto,
  // NotificationContentCode notificationContentType) {
  //
  // notificationWebServiceImpl.processSmsNotification(losSyncDto, notificationContentType);
  // notificationWebServiceImpl.processEmailNotification(losSyncDto, notificationContentType);
  //
  // }
}

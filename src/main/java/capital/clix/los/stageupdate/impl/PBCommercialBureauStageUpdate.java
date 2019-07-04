package capital.clix.los.stageupdate.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.couchbase.core.CouchbaseTemplate;
import org.springframework.stereotype.Component;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.query.N1qlQueryRow;
import capital.clix.cache.ConfigUtil;
import capital.clix.los.bean.LosSyncDto;
import capital.clix.los.bean.bl.BlLoanEntity;
import capital.clix.los.bean.cibil.ApplicantCibilReportData;
import capital.clix.los.bean.pb.PBApplicationIdsBackup;
import capital.clix.los.bean.pb.PBExcelDataApplication;
import capital.clix.los.bean.pb.PBLoanEntity;
import capital.clix.los.bean.pb.PbDelphiReport;
import capital.clix.los.couchbaseIntegration.ICouchBaseIntegration;
import capital.clix.los.enums.ApplicationStage;
import capital.clix.los.enums.ErrorState;
import capital.clix.los.enums.LoanType;
import capital.clix.los.enums.LosStatus;
import capital.clix.los.enums.NotificationContentCode;
import capital.clix.los.enums.Prefix;
import capital.clix.los.enums.Property;
import capital.clix.los.enums.WebNotificationDescription;
import capital.clix.los.exception.LosException;
import capital.clix.los.stageupdate.IStageUpdate;
import capital.clix.los.webService.ICallbackServiceImpl;
import capital.clix.los.webService.impl.NotificationWebServiceImpl;
import capital.clix.serilization.JsonMarshallingUtil;

@Component("pbCommercialBureauStageUpdate")
public class PBCommercialBureauStageUpdate implements IStageUpdate {

  @Autowired
  private CouchbaseTemplate template;

  @Autowired
  @Qualifier("couchBaseIntegrationImpl")
  private ICouchBaseIntegration couchBaseIntegrationImpl;

  @Autowired
  @Qualifier("pbCallbackServiceImpl")
  private ICallbackServiceImpl pbCallbackServiceImpl;

  @Autowired
  @Qualifier("notificationWebServiceImpl")
  private NotificationWebServiceImpl notificationWebServiceImpl;

  private static final Logger LOG = LogManager.getLogger(PBCommercialBureauStageUpdate.class);

  @Override
  public void updateApplicationStage(LosSyncDto losSyncDto, String applicationId,
      LosStatus losStatus) {

    if (losSyncDto.getErrorResponseDto() != null) {

      LOG.info(" Sending PB Error notification for application Id :{}, and stage Data : {}",
          applicationId, losSyncDto.toString());

      LOG.info("Updating the stage :{},for application id:{}", ApplicationStage.CIBIL_FAILED,
          losSyncDto.getApplicationId());

      updateApplicationStatus(Prefix.APPLICATION.getCode() + losSyncDto.getApplicationId(),
          ApplicationStage.PB_FAILED, "N", null);
      HashMap<String, String> emailData = new HashMap<>();
      emailData.put("user", losSyncDto.getApplicationId());
      emailData.put("status", "Rejected");
      emailData.put("email", "abhishek.chavan@clix.capital");
      losSyncDto.getContent().put("emailDataMap", emailData);
      // notificationWebServiceImpl.processEmailNotification(losSyncDto,
      // NotificationContentCode.SME_BUREAU);
      return;
    }


    switch (losStatus) {
      case COMMERCIAL_REQUEST_SENT: {

        if (losSyncDto.getErrorResponseDto() != null) {

        } else if (losSyncDto.getContent() != null && !losSyncDto.getContent().isEmpty()) {

          processCommercialRequestSentStageData(losSyncDto);
          // sendWebNotification(losSyncDto);
        }
        break;
      }
      case PB_DELPHI_COMMERCIAL_COMPLETE: {
        processCommercialDelphiStageData(losSyncDto);
        break;
      }
      case PB_UPDATE_COAPPLICANT_CONSUMER_FETCHED: {
        processPBUpdatedCoApplicantConsumerReportData(losSyncDto);
        break;
      }

    }
  }

  private void processCommercialDelphiStageData(LosSyncDto losSyncDto) {

    try {

      String approved = "N";
      HashMap<String, Object> finalDecisionFlagMap = new HashMap<>();
      if (losSyncDto.getContent() != null && !losSyncDto.getContent().isEmpty()) {

        if (losSyncDto.getContent().containsKey("pbDelphiReport")) {

          HashMap<String, Object> delphiDataMap =
              (HashMap<String, Object>) losSyncDto.getContent().get("pbDelphiReport");


          if (delphiDataMap != null && delphiDataMap.get("delphiResponse") != null) {

            HashMap<String, Object> delphiResponse = new HashMap<String, Object>();

            delphiResponse = (HashMap<String, Object>) delphiDataMap.get("delphiResponse");

            HashMap<String, Object> delphiData =
                (HashMap<String, Object>) delphiResponse.get("bureau_response");

            // Save PB Delphi Report
            PbDelphiReport pbDelphiReport = new PbDelphiReport();
            pbDelphiReport.setDelphiReport(delphiData);
            pbDelphiReport.setId(Prefix.DELPHI_REPORT.getCode() + losSyncDto.getApplicationId());
            template.insert(pbDelphiReport);
            // Save PB Delphi Report End

            HashMap<String, String> emailData = new HashMap<>();


            finalDecisionFlagMap = (HashMap<String, Object>) delphiData.get("final_decision_flag");

            if ("1".equalsIgnoreCase(finalDecisionFlagMap.get("derog_flag").toString())
                && "1".equalsIgnoreCase(
                    finalDecisionFlagMap.get("customer_selection_flag").toString())
                && "1".equalsIgnoreCase(finalDecisionFlagMap.get("cb_score_flag").toString())) {

              emailData.put("user", losSyncDto.getApplicationId());
              emailData.put("status", "Approved");
              emailData.put("delphi", JsonMarshallingUtil.toString(delphiData));
              emailData.put("email", "abhishek.chavan@clix.capital");
              losSyncDto.getContent().put("emailDataMap", emailData);
              approved = "Y";

              if (finalDecisionFlagMap.get("loan_amount") != null
                  && Long.valueOf(finalDecisionFlagMap.get("loan_amount").toString()) > 0) {
                updatePbExcelDataApplicationIdList(losSyncDto.getApplicationId());

              }


            } else {
              emailData.put("user", losSyncDto.getApplicationId());
              emailData.put("status", "Rejected");
              emailData.put("delphi", JsonMarshallingUtil.toString(delphiData));
              emailData.put("email", "abhishek.chavan@clix.capital");
              losSyncDto.getContent().put("emailDataMap", emailData);
            }
          }

        }

      }
      updateApplicationStatus(Prefix.APPLICATION.getCode() + losSyncDto.getApplicationId(),
          ApplicationStage.PB_FETCH_COMMERCIAL_DELPHI_REPORT_COMPLETE, approved,
          finalDecisionFlagMap);

      savePBExcelBackup(losSyncDto.getApplicationId());


      if ("Y".equalsIgnoreCase(
          ConfigUtil.getPropertyValue(Property.PB_SEND_DUMMY_EMAIL, String.class))) {
        LOG.info(" Sending PB callback for application Id :{} ", losSyncDto.getApplicationId());
        notificationWebServiceImpl.processEmailNotification(losSyncDto,
            NotificationContentCode.SME_BUREAU);
      }

    } catch (

    Exception e) {

      throw new LosException(ErrorState.INTERNAL_SERVER_ERROR,
          "Unable to process PB delphi report data processing");
    }
  }

  private void processCommercialRequestSentStageData(LosSyncDto losSyncDto) {

    LOG.info(" Processing PB Cibil Report and cibil Upload DMS Data for application id :{}",
        losSyncDto.getApplicationId());

    if (losSyncDto.getContent() != null && !losSyncDto.getContent().isEmpty()) {

      if (losSyncDto.getContent().containsKey("cibilReportData")) {
        List<Map<String, String>> cibilReportListMap =
            (ArrayList<Map<String, String>>) losSyncDto.getContent().get("cibilReportData");

        List<Map<String, String>> cibilReportDmsUploadListMap =
            (ArrayList<Map<String, String>>) losSyncDto.getContent()
                .get("PBCoapplicantDmsCibilReportUpload");

        ApplicantCibilReportData applicantCibilReportData = null;

        Map<String, ApplicantCibilReportData> applicantMap = new HashMap<>();

        if (cibilReportListMap != null && !cibilReportListMap.isEmpty()) {
          Iterator<Map<String, String>> itr = cibilReportListMap.iterator();
          while (itr.hasNext()) {
            applicantCibilReportData = new ApplicantCibilReportData();
            HashMap<String, String> map = (HashMap) itr.next();
            applicantCibilReportData
                .setId(Prefix.CIBIL_REPORT.getCode() + losSyncDto.getApplicationId()
                    + Prefix.COAPPLICANT.getCode() + map.get("applicantCode"));
            applicantCibilReportData.setPdfReport(map.get("pdfReport"));
            applicantCibilReportData.setApplicantCode(map.get("applicantCode"));

            applicantMap.put(applicantCibilReportData.getApplicantCode(), applicantCibilReportData);
          }
        }
        if (cibilReportDmsUploadListMap != null && !cibilReportDmsUploadListMap.isEmpty()) {
          Iterator<Map<String, String>> itr = cibilReportDmsUploadListMap.iterator();

          while (itr.hasNext()) {
            HashMap<String, String> map = (HashMap) itr.next();
            applicantCibilReportData = applicantMap.get(map.get("applicantCode"));
            if (applicantCibilReportData != null) {
              applicantCibilReportData.setDocumentId(map.get("documentId"));
              applicantCibilReportData.setExternalDocumentId(map.get("externalDocumentId"));
            }
          }
        }
        applicantMap.forEach((k, v) -> template.insert(v));
        updateApplicationStatus(Prefix.APPLICATION.getCode() + losSyncDto.getApplicationId(),
            ApplicationStage.CIBIL_COMPLETE_COMMERCIAL_REQUEST_SENT, "N", null);
        LOG.info(
            " successfully Processed PB Cibil Report and cibil Upload DMS Data for application id :{}",
            losSyncDto.getApplicationId());

      }
    }

  }

  private void updateApplicationStatus(String applicationId, ApplicationStage stage,
      String approved, HashMap<String, Object> finalDecisionFlagMap) {

    LOG.info(" Update stage for PB Application id :{} with stage :{}  ", applicationId, stage);
    if (applicationId.contains(":")) {
      applicationId = applicationId.split(":")[1];
    }
    N1qlQueryRow row = couchBaseIntegrationImpl.getEntityType(applicationId);
    JsonObject jsonObject = row.value();
    String type = jsonObject.getString("type");
    if (LoanType.SME_PB.name().equalsIgnoreCase(type)) {
      PBLoanEntity pbLoanEntity =
          couchBaseIntegrationImpl.getApplicationEntity(applicationId, PBLoanEntity.class);
      try {
        if (finalDecisionFlagMap != null)
          if (finalDecisionFlagMap.get("loan_amount") != null) {
            pbLoanEntity.setApprovedLoanAmount(
                Long.valueOf(finalDecisionFlagMap.get("loan_amount").toString()));
          }
        if (finalDecisionFlagMap.get("interest_rate") != null) {
          pbLoanEntity.setApprovedROI(
              Integer.valueOf(finalDecisionFlagMap.get("interest_rate").toString()));
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
      pbLoanEntity.setStatus(stage);
      pbLoanEntity.setApproved(approved);
      couchBaseIntegrationImpl.updateLoanEntity(pbLoanEntity);
      LOG.info(" successfully Updated stage for PB Application id :{} with stage :{}  ",
          applicationId, stage);
    }

  }

  private void updatePbExcelDataApplicationIdList(String appId) {

    LOG.info(" Adding PB Application into scheduler :{} ", appId);
    Calendar cal = Calendar.getInstance();
    int hour = cal.get(Calendar.HOUR_OF_DAY);

    String key =
        ConfigUtil.getPropertyValue(Property.PB_EXCEL_SCHEDULER_APPLICATION_LIST_KEY, String.class);

    PBExcelDataApplication pbExcelDataApplication =
        couchBaseIntegrationImpl.getEntity(key, PBExcelDataApplication.class);

    Map<Integer, List<String>> pbExcelDataApplicationIdListMap = null;
    List<String> applicationIdList = new ArrayList();

    if (pbExcelDataApplication == null) {

      pbExcelDataApplication = new PBExcelDataApplication();
      pbExcelDataApplicationIdListMap = new HashMap<Integer, List<String>>();

      pbExcelDataApplication.setId(key);
    } else {

      pbExcelDataApplicationIdListMap = pbExcelDataApplication.getPbExcelDataApplicationIdListMap();

      applicationIdList = pbExcelDataApplicationIdListMap.get(hour) == null ? new ArrayList()
          : pbExcelDataApplicationIdListMap.get(hour);


    }
    applicationIdList.add(appId);
    pbExcelDataApplicationIdListMap.put(hour, applicationIdList);
    pbExcelDataApplication.setPbExcelDataApplicationIdListMap(pbExcelDataApplicationIdListMap);

    LOG.info(" PB Excel scheduler after adding application id :{} is {} ", appId,
        pbExcelDataApplication.toString());
    template.save(pbExcelDataApplication);
  }

  private void savePBExcelBackup(String appId) {


    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    Calendar cal = Calendar.getInstance();
    LOG.info(" Saving application Id :{} for date wise backup with date :{}", appId, cal.getTime());
    String key = ConfigUtil.getPropertyValue(Property.PB_DAYWISE_BACKUP_KEY, String.class)
        + sdf.format(cal.getTime());
    PBApplicationIdsBackup backup =
        couchBaseIntegrationImpl.getEntity(key, PBApplicationIdsBackup.class);
    List<String> appIdList = null;
    if (backup == null) {
      backup = new PBApplicationIdsBackup();
      backup.setId(key);
      appIdList = new ArrayList<>();
    } else {
      appIdList = backup.getApplicationIds();
    }
    appIdList.add(appId);
    backup.setApplicationIds(appIdList);

    template.save(backup);
    LOG.info(" Successfully Saved application Id :{} for date wise backup with date :{}", appId,
        cal.getTime());
  }

  private void processPBUpdatedCoApplicantConsumerReportData(LosSyncDto losSyncDto) {

    LOG.info(
        " Processing PB Cibil Report and cibil Upload DMS Data for updated PB CoApplicant with application id :{}",
        losSyncDto.getApplicationId());

    if (losSyncDto.getContent() != null && !losSyncDto.getContent().isEmpty()) {

      if (losSyncDto.getContent().containsKey("pbAdditionalCoAppCibilReportData")) {
        List<Map<String, String>> cibilReportListMap = (ArrayList<Map<String, String>>) losSyncDto
            .getContent().get("pbAdditionalCoAppCibilReportData");

        List<Map<String, String>> cibilReportDmsUploadListMap =
            (ArrayList<Map<String, String>>) losSyncDto.getContent()
                .get("PBCoapplicantDmsCibilReportUpload");

        ApplicantCibilReportData applicantCibilReportData = null;

        Map<String, ApplicantCibilReportData> applicantMap = new HashMap<>();

        if (cibilReportListMap != null && !cibilReportListMap.isEmpty()) {
          Iterator<Map<String, String>> itr = cibilReportListMap.iterator();
          while (itr.hasNext()) {
            applicantCibilReportData = new ApplicantCibilReportData();
            HashMap<String, String> map = (HashMap) itr.next();
            applicantCibilReportData
                .setId(Prefix.CIBIL_REPORT.getCode() + losSyncDto.getApplicationId()
                    + Prefix.COAPPLICANT.getCode() + map.get("applicantCode"));
            applicantCibilReportData.setPdfReport(map.get("pdfReport"));
            applicantCibilReportData.setApplicantCode(map.get("applicantCode"));

            applicantMap.put(applicantCibilReportData.getApplicantCode(), applicantCibilReportData);
          }
        }
        if (cibilReportDmsUploadListMap != null && !cibilReportDmsUploadListMap.isEmpty()) {
          Iterator<Map<String, String>> itr = cibilReportDmsUploadListMap.iterator();

          while (itr.hasNext()) {
            HashMap<String, String> map = (HashMap) itr.next();
            applicantCibilReportData = applicantMap.get(map.get("applicantCode"));
            if (applicantCibilReportData != null) {
              applicantCibilReportData.setDocumentId(map.get("documentId"));
              applicantCibilReportData.setExternalDocumentId(map.get("externalDocumentId"));
            }
          }
        }
        applicantMap.forEach((k, v) -> template.insert(v));
        updateApplicationStatus(Prefix.APPLICATION.getCode() + losSyncDto.getApplicationId(),
            ApplicationStage.CIBIL_COMPLETE_COMMERCIAL_REQUEST_SENT, "N", null);
        LOG.info(
            " successfully Processed PB Cibil Report and cibil Upload DMS Data for application id :{}",
            losSyncDto.getApplicationId());

      }
    }


  }

  @Override
  public void sendErrorNotification(LosSyncDto losSyncDto,
      WebNotificationDescription webNotificationDescription) {
    // TODO Auto-generated method stub

  }
}

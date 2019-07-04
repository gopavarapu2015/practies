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
import capital.clix.los.bean.cibil.ApplicantCibilReportData;
import capital.clix.los.bean.cibil.ApplicantDelphiReportData;
import capital.clix.los.couchbaseIntegration.ICouchBaseIntegration;
import capital.clix.los.enums.ApplicationStage;
import capital.clix.los.enums.LosStatus;
import capital.clix.los.enums.NotificationContentCode;
import capital.clix.los.enums.Portfolio;
import capital.clix.los.enums.Prefix;
import capital.clix.los.enums.WebNotificationDescription;
import capital.clix.los.stageupdate.IStageUpdate;
import capital.clix.los.webService.INotificationWebService;
import capital.clix.los.webService.IWebNotificationService;
import capital.clix.los.webService.impl.PushNotificationFactory;

@Service("bureauStageUpdate")
public class BureauStageUpdate implements IStageUpdate {

  @Autowired
  private CouchbaseTemplate template;

  @Autowired
  @Qualifier("notificationWebServiceImpl")
  INotificationWebService notificationWebServiceImpl;

  @Autowired
  @Qualifier("couchBaseIntegrationImpl")
  private ICouchBaseIntegration couchBaseIntegrationImpl;

  @Autowired
  @Qualifier("pushNotificationFactory")
  PushNotificationFactory pushNotificationFactory;

  private static final Logger LOG = LogManager.getLogger(BureauStageUpdate.class);

  @Override
  public void updateApplicationStage(LosSyncDto losSyncDto, String applicationId,
      LosStatus losStatus) {

    LOG.info(" Sending Bureau Error notification for application Id :{}, and stage Data : {}",
        applicationId, losSyncDto.toString());
    if (losSyncDto.getErrorResponseDto() != null) {

      LOG.info("Updating the stage :{},for application id:{}", ApplicationStage.CIBIL_FAILED,
          losSyncDto.getApplicationId());

      updateApplicationStatus(Prefix.APPLICATION.getCode() + losSyncDto.getApplicationId(),
          ApplicationStage.CIBIL_FAILED);

      sendWebNotification(losSyncDto, losStatus, WebNotificationDescription.SME_CIBIL_FETCH_FAIL);
      return;
    }
    switch (losStatus) {
      case CIBIL_DONE: {

        if (losSyncDto.getContent() != null && !losSyncDto.getContent().isEmpty()) {

          processCibilDmsData(losSyncDto);
          sendWebNotification(losSyncDto, losStatus,
              WebNotificationDescription.SME_CIBIL_FETCH_SUCCESSFULL);

        }
        break;
      }
      case DELPHI_DONE: {

        if (losSyncDto.getContent() != null && !losSyncDto.getContent().isEmpty()) {

          processCibilDmsData(losSyncDto);

          processDephiReportData(losSyncDto);

          updateApplicationStatus(Prefix.APPLICATION.getCode() + losSyncDto.getApplicationId(),
              ApplicationStage.DELPHI_CIBIL_COMPLETE);

          sendWebNotification(losSyncDto, losStatus,
              WebNotificationDescription.SME_CIBIL_FETCH_SUCCESSFULL);

        }
        break;
      }
    }

  }

  private void processCibilDmsData(LosSyncDto losSyncDto) {

    if (losSyncDto.getContent() != null && !losSyncDto.getContent().isEmpty()) {

      if (losSyncDto.getContent().containsKey("cibilReportData")) {
        List<Map<String, String>> cibilReportListMap =
            (ArrayList<Map<String, String>>) losSyncDto.getContent().get("cibilReportData");

        List<Map<String, String>> cibilReportDmsUploadListMap =
            (ArrayList<Map<String, String>>) losSyncDto.getContent()
                .get("cibilReportDmsUploadData");

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
            ApplicationStage.CIBIL_DONE);

      }
    }
  }


  private void sendSMSEmailNotification(LosSyncDto losSyncDto,
      NotificationContentCode notificationContentType) {

    // notificationWebServiceImpl.processSmsNotification(losSyncDto, notificationContentType);
    // notificationWebServiceImpl.processEmailNotification(losSyncDto, notificationContentType);

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

  private void processDephiReportData(LosSyncDto losSyncDto) {
    if (losSyncDto.getContent() != null && !losSyncDto.getContent().isEmpty()) {

      if (losSyncDto.getContent().containsKey("coApplicantDelphiReport")) {

        List<Map<String, String>> delphiReportListMap =
            (ArrayList<Map<String, String>>) losSyncDto.getContent().get("coApplicantDelphiReport");
        ApplicantDelphiReportData applicantDelphiReportData = null;

        Map<String, ApplicantDelphiReportData> applicantMap = new HashMap<>();

        if (delphiReportListMap != null && !delphiReportListMap.isEmpty()) {
          Iterator<Map<String, String>> itr = delphiReportListMap.iterator();
          while (itr.hasNext()) {
            applicantDelphiReportData = new ApplicantDelphiReportData();
            HashMap<String, Object> map = (HashMap) itr.next();
            applicantDelphiReportData
                .setId(Prefix.DELPHI_REPORT.getCode() + losSyncDto.getApplicationId()
                    + Prefix.COAPPLICANT.getCode() + map.get("applicantCode"));
            applicantDelphiReportData.setApplicantCode(map.get("applicantCode").toString());
            applicantDelphiReportData
                .setDelphiReport((LinkedHashMap<String, String>) map.get("delphiReport"));

            applicantDelphiReportData.setSource(String.valueOf(map.get("source")));
            applicantDelphiReportData.setReportType(String.valueOf(map.get("reportType")));
            applicantMap.put(applicantDelphiReportData.getApplicantCode(),
                applicantDelphiReportData);
          }
        }
        applicantMap.forEach((k, v) -> template.insert(v));

      }
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



}

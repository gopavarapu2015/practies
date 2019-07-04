package capital.clix.los.scheduler;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.couchbase.core.CouchbaseTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.N1qlQueryResult;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import capital.clix.cache.ConfigUtil;
import capital.clix.los.bean.LosSyncDto;
import capital.clix.los.bean.pb.PBApplicationIdsBackup;
import capital.clix.los.bean.pb.PBExcelDataApplication;
import capital.clix.los.bean.pb.PBLoanEntity;
import capital.clix.los.couchbaseIntegration.ICouchBaseIntegration;
import capital.clix.los.entity.PBExcelMasterData;
import capital.clix.los.enums.NotificationContentCode;
import capital.clix.los.enums.Prefix;
import capital.clix.los.enums.Property;
import capital.clix.los.parser.IFieldParser;
import capital.clix.los.webService.INotificationWebService;
import capital.clix.util.JsonMarshallingUtil;

@Component("pBExcelGenerator")
public class PBExcelGenerator {

  @Autowired
  @Qualifier("couchBaseIntegrationImpl")
  private ICouchBaseIntegration couchBaseEgestorImpl;

  @Autowired
  private CouchbaseTemplate template;

  @Autowired
  private IFieldParser fieldParser;

  @Autowired
  @Qualifier("notificationWebServiceImpl")
  private INotificationWebService notificationWebServiceImpl;

  @Value("${scheduler.flag}")
  private Boolean flag;

  private static final Logger LOG = LogManager.getLogger(PBExcelGenerator.class);

  @Scheduled(cron = "${scheduler.cron}")
  public void scheduleFixedDelayTask() {


    if (flag) {
      try {

        PBExcelMasterData masterData = fetchExcelMasterData();
        LOG.info(" MasterData For excel Preparation loaded is :{} ", masterData.toString());

        String key = ConfigUtil.getPropertyValue(Property.PB_EXCEL_SCHEDULER_APPLICATION_LIST_KEY,
            String.class);

        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);

        PBExcelDataApplication pbExcelDataApplication =
            couchBaseEgestorImpl.getEntity(key, PBExcelDataApplication.class);

        Map<Integer, List<String>> applicationIdListMap =
            pbExcelDataApplication.getPbExcelDataApplicationIdListMap();

        LOG.info(" Application Ids loaded for Excel Generation is : {}", applicationIdListMap);

        if (applicationIdListMap != null) {
          Workbook wb = new XSSFWorkbook();
          String safeName = WorkbookUtil.createSafeSheetName(
              ConfigUtil.getPropertyValue(Property.PB_EXCEL_SHEET_NAME, String.class) + new Date());
          Sheet sheet = wb.createSheet(safeName);
          LOG.info(" Sheet created ");
          Row row = sheet.createRow(0);
          int i = 0;
          for (String column : masterData.getColumnList()) {
            row.createCell(i).setCellValue(column);
            i++;
          }
          LOG.info(" Column Added in Excel sheet");
          int j = 0;
          List<Integer> keyToRemove = new ArrayList();
          Set<Integer> mapKey = applicationIdListMap.keySet();
          LOG.info(" Map Keys fetched:{} ", mapKey);
          Iterator<Integer> itr = mapKey.iterator();
          List<String> applicationIds = null;
          while (itr.hasNext()) {

            Integer keyVal = itr.next();
            keyToRemove.add(keyVal);
            LOG.info(" Started Processing Application for Excel with hour key :{} ", keyVal);
            if (keyVal == hour) {
              continue;
            }
            applicationIds = applicationIdListMap.get(keyVal);
            LOG.info(" application fetched against key:{} ", keyVal);
            for (String appId : applicationIds) {
              j++;
              LOG.info("Processing Application id :{} for excel ", appId);
              PBLoanEntity pbEntity =
                  couchBaseEgestorImpl.getApplicationEntity(appId, PBLoanEntity.class);
              LOG.info("PB entity Loaded :{}", appId);

              String delphiKey = Prefix.DELPHI_REPORT.getCode() + appId;

              N1qlQueryResult result = template.queryN1QL(N1qlQuery.simple(
                  "SELECT * FROM " + ConfigUtil.getPropertyValue(Property.LOS_BUCKET, String.class)
                      + " where meta().id = '" + delphiKey + "'"));

              LinkedTreeMap applicationData =
                  new Gson().fromJson(JsonMarshallingUtil.toString(pbEntity), LinkedTreeMap.class);

              LinkedTreeMap delphiData =
                  new Gson().fromJson(result.allRows().get(0).toString(), LinkedTreeMap.class);

              LOG.info("PB Delphi Tree Map :{}", delphiData);
              Map<String, String> fieldsMaster = new HashMap<>();
              masterData.getSourceFieldsListMap().forEach((id, fields) -> {

                if ("LOS_APPLICATION_DATA".equalsIgnoreCase(id)) {
                  fieldsMaster.putAll(fieldParser.parseForFields(fields, applicationData));
                } else if ("LOS_APPLICATION_DELPHI_DATA".equalsIgnoreCase(id)) {
                  fieldsMaster.putAll(fieldParser.parseForFields(fields, delphiData));
                }
              });
              fieldsMaster.put("Application Id", JsonMarshallingUtil.toString(appId));
              LOG.info(" Field Parser Ready for AppIs :{}", appId);
              try {

                row = sheet.createRow(j);
                i = 0;
                LOG.info(" Column List Size: {}", masterData.getColumnList().size());
                for (String column : masterData.getColumnList()) {

                  System.out.println(fieldsMaster.get(column));
                  if (column.equalsIgnoreCase("application date")) {
                    Long val = capital.clix.serilization.JsonMarshallingUtil
                        .fromString(fieldsMaster.get(column), Long.class);

                    System.out.println(val);
                    Date date = new Date(val);
                    row.createCell(i).setCellValue(date.toString());
                  } else {
                    row.createCell(i).setCellValue(capital.clix.serilization.JsonMarshallingUtil
                        .fromString(fieldsMaster.get(column), String.class));
                  }
                  i++;
                }

              } catch (Exception e) {
                e.printStackTrace();

              }
            }
            LOG.info("Removing hour key :{}", keyVal);
            // applicationIdListMap.remove(keyVal);
          }

          for (Integer val : keyToRemove) {
            applicationIdListMap.remove(val);
          }
          pbExcelDataApplication.setPbExcelDataApplicationIdListMap(applicationIdListMap);
          template.save(pbExcelDataApplication);

          // FileOutputStream fileOut = new FileOutputStream("/home/CLIX/100405/hello.xlsx");
          // wb.write(fileOut);
          ByteArrayOutputStream bos = new ByteArrayOutputStream();
          try {
            wb.write(bos);
            LOG.info(" Byte Stream Written Successfully ");
          } catch (Exception e) {
            LOG.info(" Error while writing Byte Stream ");
          } finally {
            bos.close();
          }
          byte[] bytes = bos.toByteArray();
          String base64Encoded = Base64.getEncoder().encodeToString(bytes);

          System.out.println(base64Encoded);
          // fileOut.close();
          wb.close();
          LOG.info("WorkBook Closed successfully");
          LosSyncDto losSyncDto = new LosSyncDto();
          HashMap<String, String> mailAttachment = new HashMap<>();
          mailAttachment.put("content", base64Encoded);
          mailAttachment.put("fileName",
              ConfigUtil.getPropertyValue(Property.PB_EXCEL_NAME, String.class));
          mailAttachment.put("extension",
              ConfigUtil.getPropertyValue(Property.PB_FILE_EXTENSION, String.class));
          losSyncDto.getContent().put("mailAttachment", mailAttachment);
          LOG.info(" Mailing Excel sheet ");
          notificationWebServiceImpl.processEmailNotification(losSyncDto,
              NotificationContentCode.PB_MIS_REPORT);


        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  private PBExcelMasterData fetchExcelMasterData() {
    return couchBaseEgestorImpl.getEntity(
        ConfigUtil.getPropertyValue(Property.PB_EXCEL_REPORT_FIELD_MAPPING, String.class),
        PBExcelMasterData.class);
  }


  @Scheduled(cron = "${scheduler.cron1}")
  public void previousDayMIS() {

    String date = null;
    if (flag) {
      try {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -1);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        date = sdf.format(cal.getTime());
        String key =
            ConfigUtil.getPropertyValue(Property.PB_DAYWISE_BACKUP_KEY, String.class) + date;
        LOG.info("Key Formed is :{} ", key);
        PBApplicationIdsBackup dateWiseBackup =
            couchBaseEgestorImpl.getEntity(key, PBApplicationIdsBackup.class);

        if (dateWiseBackup == null || dateWiseBackup.getApplicationIds() == null
            || dateWiseBackup.getApplicationIds().size() == 0) {
          LOG.info("No PB Application ID found for Date :{} to generate excel MIS", date);
          return;
        }

        PBExcelMasterData masterData = fetchExcelMasterData();

        Workbook wb = new XSSFWorkbook();
        String safeName = WorkbookUtil.createSafeSheetName(
            ConfigUtil.getPropertyValue(Property.PB_EXCEL_SHEET_NAME, String.class) + new Date());
        Sheet sheet = wb.createSheet(safeName);
        LOG.info(" Sheet created ");
        Row row = sheet.createRow(0);
        int i = 0;
        for (String column : masterData.getColumnList()) {
          row.createCell(i).setCellValue(column);
          i++;
        }
        LOG.info(" Column Added in Excel sheet");

        int j = 0;
        for (String appId : dateWiseBackup.getApplicationIds()) {
          j++;
          LOG.info("Processing Application id :{} for excel ", appId);
          PBLoanEntity pbEntity =
              couchBaseEgestorImpl.getApplicationEntity(appId, PBLoanEntity.class);
          LOG.info("PB entity Loaded :{}", appId);
          String delphiKey = Prefix.DELPHI_REPORT.getCode() + appId;

          System.out.println("Delphi Key formed is :{}" + delphiKey);

          N1qlQueryResult result = template.queryN1QL(N1qlQuery.simple(
              "SELECT * FROM " + ConfigUtil.getPropertyValue(Property.LOS_BUCKET, String.class)
                  + " where meta().id = '" + delphiKey + "'"));


          LinkedTreeMap applicationData =
              new Gson().fromJson(JsonMarshallingUtil.toString(pbEntity), LinkedTreeMap.class);

          LinkedTreeMap delphiData =
              new Gson().fromJson(result.allRows().get(0).toString(), LinkedTreeMap.class);

          LOG.info("PB Delphi Tree Map :{}", delphiData);
          Map<String, String> fieldsMaster = new HashMap<>();
          masterData.getSourceFieldsListMap().forEach((id, fields) -> {

            if ("LOS_APPLICATION_DATA".equalsIgnoreCase(id)) {
              fieldsMaster.putAll(fieldParser.parseForFields(fields, applicationData));
            } else if ("LOS_APPLICATION_DELPHI_DATA".equalsIgnoreCase(id)) {
              fieldsMaster.putAll(fieldParser.parseForFields(fields, delphiData));
            }
          });

          fieldsMaster.put("Application Id", JsonMarshallingUtil.toString(appId));
          LOG.info(" Field Parser Ready for AppIs :{}", appId);
          try {

            row = sheet.createRow(j);
            i = 0;
            LOG.info(" Column List Size: {}", masterData.getColumnList().size());
            for (String column : masterData.getColumnList()) {

              if (column.equalsIgnoreCase("application date")) {
                Long val = capital.clix.serilization.JsonMarshallingUtil
                    .fromString(fieldsMaster.get(column), Long.class);

                // System.out.println(val);
                Date date1 = new Date(val);
                row.createCell(i).setCellValue(date1.toString());
              } else {
                row.createCell(i).setCellValue(capital.clix.serilization.JsonMarshallingUtil
                    .fromString(fieldsMaster.get(column), String.class));
              }
              i++;
            }

          } catch (Exception e) {
            e.printStackTrace();

          }
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
          wb.write(bos);
          LOG.info(" Byte Stream Written Successfully ");
        } catch (Exception e) {
          LOG.info(" Error while writing Byte Stream ");
        } finally {
          bos.close();
        }
        byte[] bytes = bos.toByteArray();
        String base64Encoded = Base64.getEncoder().encodeToString(bytes);
        wb.close();
        LOG.info("WorkBook Closed successfully");
        LosSyncDto losSyncDto = new LosSyncDto();
        HashMap<String, String> mailAttachment = new HashMap<>();
        mailAttachment.put("content", base64Encoded);
        mailAttachment.put("fileName",
            ConfigUtil.getPropertyValue(Property.PB_EXCEL_NAME, String.class));
        mailAttachment.put("extension",
            ConfigUtil.getPropertyValue(Property.PB_FILE_EXTENSION, String.class));
        losSyncDto.getContent().put("mailAttachment", mailAttachment);
        LOG.info(" Mailing Excel sheet ");
        notificationWebServiceImpl.processEmailNotification(losSyncDto,
            NotificationContentCode.PB_MIS_REPORT);
      } catch (Exception e) {
        e.printStackTrace();
        LOG.info(" Error Occured while creating MIS for previos Day date :{}", date);
      }
    }
  }

  public void dateWisePBCases(String date) {

    try {
      Calendar cal = Calendar.getInstance();
      cal.add(Calendar.DAY_OF_MONTH, -1);
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
      if (date == null) {
        date = sdf.format(cal.getTime());
      }
      String key = "PB:Application-Created-" + date;
      LOG.info("Key Formed is :{} ", key);
      PBApplicationIdsBackup dateWiseBackup =
          couchBaseEgestorImpl.getEntity(key, PBApplicationIdsBackup.class);

      if (dateWiseBackup == null || dateWiseBackup.getApplicationIds() == null
          || dateWiseBackup.getApplicationIds().size() == 0) {
        LOG.info("No PB Application ID found for Date :{} to generate excel MIS", date);
        return;
      }

      PBExcelMasterData masterData = fetchExcelMasterData();

      Workbook wb = new XSSFWorkbook();
      String safeName = WorkbookUtil.createSafeSheetName(
          ConfigUtil.getPropertyValue(Property.PB_EXCEL_SHEET_NAME, String.class) + new Date());
      Sheet sheet = wb.createSheet(safeName);
      LOG.info(" Sheet created ");
      Row row = sheet.createRow(0);
      int i = 0;
      for (String column : masterData.getColumnList()) {
        row.createCell(i).setCellValue(column);
        i++;
      }
      LOG.info(" Column Added in Excel sheet");

      int j = 0;
      for (String appId : dateWiseBackup.getApplicationIds()) {
        j++;
        LOG.info("Processing Application id :{} for excel ", appId);
        PBLoanEntity pbEntity =
            couchBaseEgestorImpl.getApplicationEntity(appId, PBLoanEntity.class);
        LOG.info("PB entity Loaded :{}", appId);

        String delphiKey = Prefix.DELPHI_REPORT.getCode() + appId;

        System.out.println("Delphi Key formed is :{}" + delphiKey);

        N1qlQueryResult result = template.queryN1QL(N1qlQuery.simple(
            "SELECT * FROM " + ConfigUtil.getPropertyValue(Property.LOS_BUCKET, String.class)
                + " where meta().id = '" + delphiKey + "'"));

        LinkedTreeMap applicationData =
            new Gson().fromJson(JsonMarshallingUtil.toString(pbEntity), LinkedTreeMap.class);

        LinkedTreeMap delphiData =
            new Gson().fromJson(result.allRows().get(0).toString(), LinkedTreeMap.class);

        LOG.info("PB Delphi Tree Map :{}", delphiData);
        Map<String, String> fieldsMaster = new HashMap<>();
        masterData.getSourceFieldsListMap().forEach((id, fields) -> {

          if ("LOS_APPLICATION_DATA".equalsIgnoreCase(id)) {
            fieldsMaster.putAll(fieldParser.parseForFields(fields, applicationData));
          } else if ("LOS_APPLICATION_DELPHI_DATA".equalsIgnoreCase(id)) {
            fieldsMaster.putAll(fieldParser.parseForFields(fields, delphiData));
          }
        });

        fieldsMaster.put("Application Id", JsonMarshallingUtil.toString(appId));
        LOG.info(" Field Parser Ready for AppIs :{}", appId);
        try {

          row = sheet.createRow(j);
          i = 0;
          LOG.info(" Column List Size: {}", masterData.getColumnList().size());
          for (String column : masterData.getColumnList()) {

            if (column.equalsIgnoreCase("application date")) {
              Long val = capital.clix.serilization.JsonMarshallingUtil
                  .fromString(fieldsMaster.get(column), Long.class);

              System.out.println(val);
              Date date1 = new Date(val);
              row.createCell(i).setCellValue(date1.toString());
            } else {
              row.createCell(i).setCellValue(capital.clix.serilization.JsonMarshallingUtil
                  .fromString(fieldsMaster.get(column), String.class));
            }
            i++;
          }

        } catch (Exception e) {
          e.printStackTrace();

        }
      }

      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      try {
        wb.write(bos);
        LOG.info(" Byte Stream Written Successfully ");
      } catch (Exception e) {
        LOG.info(" Error while writing Byte Stream ");
      } finally {
        bos.close();
      }
      byte[] bytes = bos.toByteArray();
      String base64Encoded = Base64.getEncoder().encodeToString(bytes);
      wb.close();
      LOG.info("WorkBook Closed successfully");
      LosSyncDto losSyncDto = new LosSyncDto();
      HashMap<String, String> mailAttachment = new HashMap<>();
      mailAttachment.put("content", base64Encoded);
      mailAttachment.put("fileName",
          ConfigUtil.getPropertyValue(Property.PB_EXCEL_NAME, String.class));
      mailAttachment.put("extension",
          ConfigUtil.getPropertyValue(Property.PB_FILE_EXTENSION, String.class));
      losSyncDto.getContent().put("mailAttachment", mailAttachment);
      LOG.info(" Mailing Excel sheet ");
      notificationWebServiceImpl.processEmailNotification(losSyncDto,
          NotificationContentCode.PB_MIS_REPORT);
    } catch (Exception e) {
      e.printStackTrace();
      LOG.info(" Error Occured while creating MIS for previos Day date :{}", date);
    }
  }

  public String prepareFromPBIds(List<String> pbIdList) {

    try {
      PBExcelMasterData masterData = fetchExcelMasterData();

      if (pbIdList == null || pbIdList.size() == 0) {
        return "blank";
      }

      Workbook wb = new XSSFWorkbook();
      String safeName = WorkbookUtil.createSafeSheetName(
          ConfigUtil.getPropertyValue(Property.PB_EXCEL_SHEET_NAME, String.class) + new Date());
      Sheet sheet = wb.createSheet(safeName);
      LOG.info(" Sheet created ");
      Row row = sheet.createRow(0);
      int i = 0;
      for (String column : masterData.getColumnList()) {
        row.createCell(i).setCellValue(column);
        i++;
      }
      LOG.info(" Column Added in Excel sheet");
      int j = 0;
      for (String appId : pbIdList) {
        j++;
        LOG.info("Processing Application id :{} for excel ", appId);
        PBLoanEntity pbEntity =
            couchBaseEgestorImpl.getApplicationEntity(appId, PBLoanEntity.class);

        String delphiKey = Prefix.DELPHI_REPORT.getCode() + appId;

        LOG.info("Delphi Key formed is :{}" + delphiKey);

        N1qlQueryResult result = template.queryN1QL(N1qlQuery.simple(
            "SELECT * FROM " + ConfigUtil.getPropertyValue(Property.LOS_BUCKET, String.class)
                + " where meta().id = '" + delphiKey + "'"));


        if (result == null || result.allRows() == null || result.allRows().size() == 0
            || pbEntity == null) {
          LOG.info(" Escaping application id :{} ", appId);
          continue;
        }
        LinkedTreeMap applicationData =
            new Gson().fromJson(JsonMarshallingUtil.toString(pbEntity), LinkedTreeMap.class);

        LinkedTreeMap delphiData =
            new Gson().fromJson(result.allRows().get(0).toString(), LinkedTreeMap.class);

        LOG.info("PB Delphi Tree Map :{}", delphiData);
        Map<String, String> fieldsMaster = new HashMap<>();
        masterData.getSourceFieldsListMap().forEach((id, fields) -> {

          if ("LOS_APPLICATION_DATA".equalsIgnoreCase(id)) {
            fieldsMaster.putAll(fieldParser.parseForFields(fields, applicationData));
          } else if ("LOS_APPLICATION_DELPHI_DATA".equalsIgnoreCase(id)) {
            fieldsMaster.putAll(fieldParser.parseForFields(fields, delphiData));
          }
        });

        fieldsMaster.put("Application Id", JsonMarshallingUtil.toString(appId));
        LOG.info(" Field Parser Ready for AppIs :{}", appId);
        try {

          row = sheet.createRow(j);
          i = 0;
          LOG.info(" Column List Size: {}", masterData.getColumnList().size());
          for (String column : masterData.getColumnList()) {

            if (column.equalsIgnoreCase("application date")) {
              Long val = capital.clix.serilization.JsonMarshallingUtil
                  .fromString(fieldsMaster.get(column), Long.class);

              System.out.println(val);
              Date date1 = new Date(val);
              row.createCell(i).setCellValue(date1.toString());
            } else {
              row.createCell(i).setCellValue(capital.clix.serilization.JsonMarshallingUtil
                  .fromString(fieldsMaster.get(column), String.class));
            }
            i++;
          }

        } catch (Exception e) {
          e.printStackTrace();

        }
      }

      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      try {
        wb.write(bos);
        LOG.info(" Byte Stream Written Successfully ");
      } catch (Exception e) {
        LOG.info(" Error while writing Byte Stream ");
      } finally {
        bos.close();
      }
      byte[] bytes = bos.toByteArray();
      String base64Encoded = Base64.getEncoder().encodeToString(bytes);
      wb.close();
      LOG.info("WorkBook Closed successfully");
      LosSyncDto losSyncDto = new LosSyncDto();
      HashMap<String, String> mailAttachment = new HashMap<>();
      mailAttachment.put("content", base64Encoded);
      mailAttachment.put("fileName",
          ConfigUtil.getPropertyValue(Property.PB_EXCEL_NAME, String.class));
      mailAttachment.put("extension",
          ConfigUtil.getPropertyValue(Property.PB_FILE_EXTENSION, String.class));
      losSyncDto.getContent().put("mailAttachment", mailAttachment);
      LOG.info(" Mailing Excel sheet ");
      notificationWebServiceImpl.processEmailNotification(losSyncDto,
          NotificationContentCode.PB_MIS_REPORT);
    } catch (Exception e) {
      e.printStackTrace();
      return "Exception";
    }
    return "success";
  }

}

/**
 * Copyright 2018 CLix Capital (P) Limited . All Rights Reserved. Clix Capital
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * @author vishalswami
 * @version 1.0, 20 Mar 18
 */

package capital.clix.los.enums;

import capital.clix.cache.IBaseProperty;

public enum Property implements IBaseProperty {

  KAFKA_BOOTSTRAP_URL("kafka.bootstrap.url", null, String.class),
  WEB_NOTIFICATION_VERSION("web.notification.version", "v2", String.class),
  WEB_NOTIFICATION_SERVICE_NAME("web.notification.service.name", "GenericStatusUpdatePush", String.class),
  NOTIFICATION_APPID("notification.appid", "clcalt", String.class),
  NOTIFICATION_SUB_APPID("notification.sub.appid", "clcalt", String.class),
  NOTIFICATION_SMS_URL("notification.sms.url", "http://10.108.1.19/notification/v1/sms", String.class),
  NOTIFICATION_EMAIL_URL("notification.email.url", "http://10.108.1.19/notification/v1/email", String.class),
  NOTIFICATION_FROM("notification.from", "MYCLIX", String.class),
  WORKFLOW_URL("workflow.url", "http://localhost:8081/we/v1/initiate",String.class),
  PB_SME_CALLBACK_URL("pb.sme.callback.url", "http://localhost:8089/pb",String.class),
  CURRENT_ACCOUNT("current.account", "none",String.class),
  OVERDRAFT("overdraft", "OD",String.class),
  CASH_CREDIT("cash.credit", "CC",String.class),
  CACHE_RELOAD_PASSWORD("cache.reload.password", "clix@123", String.class),
  KAFKA_RETRY_TOPIC("kafka.retry.topic", "WE-RETRY", String.class),
  LOS_BUCKET("los.bucket", "los", String.class),
  PB_START_HOUR("pb.start.hour", "14", String.class),
  PB_END_HOUR("pb.end.hour", "18", String.class),
  SME_REPORT_STAGE_MAPPING_KEY("sme.report.stage.mapping.key", "SME:REPORT-STAGE", String.class),
  PB_TEMP_EMAIL("pb.temp.email", "naveen.jain@clix.capital", String.class),
  PB_EXCEL_SCHEDULER_APPLICATION_LIST_KEY("pb.excel.scheduler.application.list.key", "PB:Excel-Data-Scheduler-Application-List",String.class),
  PB_EXCEL_SHEET_NAME("pb.excel.sheet.name", "PB:Approved-Case-",String.class),
  PB_EXCEL_NAME("pb.excel.name", "PB-Clix-Approved-Case",String.class),
  PB_FILE_EXTENSION("pb.file.extension", "xlsx",String.class),
  PB_SOURCE("pb.source", "SME_PB",String.class),
  PB_ZONE("pb.zone", "Asia/Kolkata",String.class),
  PB_SEND_DUMMY_EMAIL("pb.send.dummy.email", "N",String.class),
  PB_DAYWISE_BACKUP_KEY("pb.daywise.backup.key", "PB:Application-Created-",String.class),
  PB_EXCEL_REPORT_FIELD_MAPPING("pb.excel.report.field.mapping", "PB:Excel-Report-Field-Mapping",String.class),
  KAFKA_TOPIC("kafka.topic", "testing", String.class);


  private String name;
  private String value;
  private Class type;

  Property(String name, String value, Class type) {
    this.name = name;
    this.value = value;
    this.type = type;
  }

  public String getName() {
    return name;
  }

  public String getValue() {
    return value;
  }

  public Class getType() {
    return type;
  }
}

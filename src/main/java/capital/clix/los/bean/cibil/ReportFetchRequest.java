package capital.clix.los.bean.cibil;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import capital.clix.los.enums.ReportStage;
import capital.clix.los.enums.ReportType;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = Include.NON_NULL)
public class ReportFetchRequest {

  private String applicationId;
  private ReportType reportType;
  private ReportStage reportStage;


  public String getApplicationId() {
    return applicationId;
  }

  public void setApplicationId(String applicationId) {
    this.applicationId = applicationId;
  }

  public ReportType getReportType() {
    return reportType;
  }

  public void setReportType(ReportType reportType) {
    this.reportType = reportType;
  }

  public ReportStage getReportStage() {
    return reportStage;
  }

  public void setReportStage(ReportStage reportStage) {
    this.reportStage = reportStage;
  }

  @Override
  public String toString() {
    return "ReportFetchRequest [applicationId=" + applicationId + ", reportType=" + reportType
        + ", reportStage=" + reportStage + "]";
  }



}

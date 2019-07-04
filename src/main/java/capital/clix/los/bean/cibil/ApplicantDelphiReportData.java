package capital.clix.los.bean.cibil;

import java.util.LinkedHashMap;
import org.springframework.data.couchbase.core.mapping.Document;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import capital.clix.los.bean.BaseEntity;

@Document
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApplicantDelphiReportData extends BaseEntity {

  private String applicantCode;
  private String source;
  private String reportType;
  private LinkedHashMap delphiReport;

  public String getApplicantCode() {
    return applicantCode;
  }

  public void setApplicantCode(String applicantCode) {
    this.applicantCode = applicantCode;
  }

  public LinkedHashMap getDelphiReport() {
    return delphiReport;
  }

  public void setDelphiReport(LinkedHashMap delphiReport) {
    this.delphiReport = delphiReport;
  }

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public String getReportType() {
    return reportType;
  }

  public void setReportType(String reportType) {
    this.reportType = reportType;
  }

  @Override
  public String toString() {
    return "ApplicantDelphiReportData [applicantCode=" + applicantCode + ", source=" + source
        + ", reportType=" + reportType + ", delphiReport=" + delphiReport + "]";
  }



}

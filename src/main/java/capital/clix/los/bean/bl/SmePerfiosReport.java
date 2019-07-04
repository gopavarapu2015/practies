package capital.clix.los.bean.bl;

import java.util.Map;
import org.springframework.data.couchbase.core.mapping.Document;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import capital.clix.los.bean.BaseEntity;
import capital.clix.los.bean.ErrorResponseDto;
import capital.clix.util.JsonMarshallingUtil;

@Document
@JsonIgnoreProperties(ignoreUnknown = true)
public class SmePerfiosReport extends BaseEntity {

  private String loanApplicationId;
  private String applicantReports;
  private Map<String, Object> perfiosExcelReportData;
  private SmePerfiosDelphiReport smePerfiosDelphiReport;
  private Boolean success;
  private ErrorResponseDto errorResponseDto;
  private String accountHolderName;
  private String accountType;
  private String sanctionLimit;
  private String yearMonthTo;
  private String yearMonthFrom;

  private static final Gson GSON = new Gson();

  public String getLoanApplicationId() {
    return loanApplicationId;
  }

  public void setLoanApplicationId(String loanApplicationId) {
    this.loanApplicationId = loanApplicationId;
  }

  public LinkedTreeMap getApplicantReports() {

    return GSON.fromJson(this.applicantReports, LinkedTreeMap.class);
  }

  public void setApplicantReports(LinkedTreeMap applicantReports) {
    this.applicantReports = JsonMarshallingUtil.toString(applicantReports);
  }

  public Map<String, Object> getPerfiosExcelReportData() {
    return perfiosExcelReportData;
  }

  public void setPerfiosExcelReportData(Map<String, Object> perfiosExcelReportData) {
    this.perfiosExcelReportData = perfiosExcelReportData;
  }

  public Boolean getSuccess() {
    return success;
  }

  public void setSuccess(Boolean success) {
    this.success = success;
  }

  public SmePerfiosDelphiReport getSmePerfiosDelphiReport() {
    return smePerfiosDelphiReport;
  }

  public void setSmePerfiosDelphiReport(SmePerfiosDelphiReport smePerfiosDelphiReport) {
    this.smePerfiosDelphiReport = smePerfiosDelphiReport;
  }

  public ErrorResponseDto getErrorResponseDto() {
    return errorResponseDto;
  }

  public void setErrorResponseDto(ErrorResponseDto errorResponseDto) {
    this.errorResponseDto = errorResponseDto;
  }

  public String getAccountHolderName() {
    return accountHolderName;
  }

  public void setAccountHolderName(String accountHolderName) {
    this.accountHolderName = accountHolderName;
  }

  public String getAccountType() {
    return accountType;
  }

  public void setAccountType(String accountType) {
    this.accountType = accountType;
  }

  public String getSanctionLimit() {
    return sanctionLimit;
  }

  public void setSanctionLimit(String sanctionLimit) {
    this.sanctionLimit = sanctionLimit;
  }

  public String getYearMonthTo() {
    return yearMonthTo;
  }

  public void setYearMonthTo(String yearMonthTo) {
    this.yearMonthTo = yearMonthTo;
  }

  public String getYearMonthFrom() {
    return yearMonthFrom;
  }

  public void setYearMonthFrom(String yearMonthFrom) {
    this.yearMonthFrom = yearMonthFrom;
  }

  @Override
  public String toString() {
    return "SmePerfiosReport [loanApplicationId=" + loanApplicationId + ", applicantReports="
        + applicantReports + ", perfiosExcelReportData=" + perfiosExcelReportData
        + ", smePerfiosDelphiReport=" + smePerfiosDelphiReport + ", success=" + success
        + ", errorResponseDto=" + errorResponseDto + ", accountHolderName=" + accountHolderName
        + ", accountType=" + accountType + ", sanctionLimit=" + sanctionLimit + "]";
  }

}

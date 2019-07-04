package capital.clix.los.bean.bl;

import java.util.LinkedHashMap;

public class SmePerfiosDelphiReport {

  private String reportType;
  private String loanApplicationId;
  private String requestId;
  private String source;
  private LinkedHashMap perfiodDelphiReport;

  public String getReportType() {
    return reportType;
  }

  public void setReportType(String reportType) {
    this.reportType = reportType;
  }

  public String getLoanApplicationId() {
    return loanApplicationId;
  }

  public void setLoanApplicationId(String loanApplicationId) {
    this.loanApplicationId = loanApplicationId;
  }

  public String getRequestId() {
    return requestId;
  }

  public void setRequestId(String requestId) {
    this.requestId = requestId;
  }

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public LinkedHashMap getPerfiodDelphiReport() {
    return perfiodDelphiReport;
  }

  public void setPerfiodDelphiReport(LinkedHashMap perfiodDelphiReport) {
    this.perfiodDelphiReport = perfiodDelphiReport;
  }

  @Override
  public String toString() {
    return "SmePerfiosDelphiReport [reportType=" + reportType + ", loanApplicationId="
        + loanApplicationId + ", requestId=" + requestId + ", source=" + source
        + ", perfiodDelphiReport=" + perfiodDelphiReport + "]";
  }



}

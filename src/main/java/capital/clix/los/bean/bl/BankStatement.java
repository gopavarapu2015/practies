package capital.clix.los.bean.bl;

import java.util.List;

public class BankStatement {

  private List<String> dmsId;
  private String institutionCode;
  private String yearMonthFrom;
  private String yearMonthTo;
  private Boolean scannedStatement;


  public List<String> getDmsId() {
    return dmsId;
  }

  public void setDmsId(List<String> dmsId) {
    this.dmsId = dmsId;
  }

  public String getInstitutionCode() {
    return institutionCode;
  }

  public void setInstitutionCode(String institutionCode) {
    this.institutionCode = institutionCode;
  }

  public String getYearMonthFrom() {
    return yearMonthFrom;
  }

  public void setYearMonthFrom(String yearMonthFrom) {
    this.yearMonthFrom = yearMonthFrom;
  }

  public String getYearMonthTo() {
    return yearMonthTo;
  }

  public void setYearMonthTo(String yearMonthTo) {
    this.yearMonthTo = yearMonthTo;
  }

  public Boolean getScannedStatement() {
    return scannedStatement;
  }

  public void setScannedStatement(Boolean scannedStatement) {
    this.scannedStatement = scannedStatement;
  }


}

package capital.clix.los.bean.cibil;

import java.util.LinkedHashMap;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import capital.clix.los.bean.fsa.BalanceSummaryReport;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = Include.NON_NULL)
public class SmeReportFetchResponse {

  private String name;
  private String pan;
  private String mobile;
  private String applicationId;
  private String externalDocumentId;
  private String documentId;
  private String applicantCode;
  private LinkedHashMap<String, String> delphiReport;
  private LinkedHashMap<String, String> perfiosDelphiReport;
  private String month;
  private String gstn1;
  private String gstn2;
  private String gstn3;
  private List<BalanceSummaryReport> fsaBalanceSummaryReport;
public String getMonth() {
	return month;
}

public void setMonth(String month) {
	this.month = month;
}

public String getGstn1() {
	return gstn1;
}

public void setGstn1(String gstn1) {
	this.gstn1 = gstn1;
}

public String getGstn2() {
	return gstn2;
}

public void setGstn2(String gstn2) {
	this.gstn2 = gstn2;
}

public String getGstn3() {
	return gstn3;
}

public void setGstn3(String gstn3) {
	this.gstn3 = gstn3;
}

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPan() {
    return pan;
  }

  public void setPan(String pan) {
    this.pan = pan;
  }

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  public String getApplicationId() {
    return applicationId;
  }

  public void setApplicationId(String applicationId) {
    this.applicationId = applicationId;
  }

  public String getExternalDocumentId() {
    return externalDocumentId;
  }

  public void setExternalDocumentId(String externalDocumentId) {
    this.externalDocumentId = externalDocumentId;
  }

  public String getDocumentId() {
    return documentId;
  }

  public void setDocumentId(String documentId) {
    this.documentId = documentId;
  }

  public String getApplicantCode() {
    return applicantCode;
  }

  public void setApplicantCode(String applicantCode) {
    this.applicantCode = applicantCode;
  }

  public LinkedHashMap<String, String> getDelphiReport() {
    return delphiReport;
  }

  public void setDelphiReport(LinkedHashMap<String, String> delphiReport) {
    this.delphiReport = delphiReport;
  }

  public LinkedHashMap<String, String> getPerfiosDelphiReport() {
    return perfiosDelphiReport;
  }

  public void setPerfiosDelphiReport(LinkedHashMap<String, String> perfiosDelphiReport) {
    this.perfiosDelphiReport = perfiosDelphiReport;
  }

	public List<BalanceSummaryReport> getFsaBalanceSummaryReport() {
		return fsaBalanceSummaryReport;
	}

	public void setFsaBalanceSummaryReport(List<BalanceSummaryReport> fsaBalanceSummaryReport) {
		this.fsaBalanceSummaryReport = fsaBalanceSummaryReport;
	}

	@Override
	public String toString() {
		return "SmeReportFetchResponse [name=" + name + ", pan=" + pan + ", mobile=" + mobile + ", applicationId="
				+ applicationId + ", externalDocumentId=" + externalDocumentId + ", documentId=" + documentId
				+ ", applicantCode=" + applicantCode + ", delphiReport=" + delphiReport + ", perfiosDelphiReport="
				+ perfiosDelphiReport + ", fsaBalanceSummaryReport=" + fsaBalanceSummaryReport + "]";
	}

}

package capital.clix.los.bean.cibil;

import org.springframework.data.couchbase.core.mapping.Document;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import capital.clix.los.bean.BaseEntity;

@JsonIgnoreProperties(ignoreUnknown = true)
@Document
public class ApplicantCibilReportData extends BaseEntity {

  private String externalDocumentId;
  private String pdfReport;
  private String documentId;
  private String applicantCode;

  public String getExternalDocumentId() {
    return externalDocumentId;
  }

  public void setExternalDocumentId(String externalDocumentId) {
    this.externalDocumentId = externalDocumentId;
  }

  public String getPdfReport() {
    return pdfReport;
  }

  public void setPdfReport(String pdfReport) {
    this.pdfReport = pdfReport;
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



}

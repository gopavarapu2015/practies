package capital.clix.los.bean.cibil;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CibilReportBureauResponse {

  private String bureauName;
  private String xmlResponse;
  private String pdfResponse;
  private String htmlResponse;

  public String getBureauName() {
    return bureauName;
  }

  public void setBureauName(String bureauName) {
    this.bureauName = bureauName;
  }

  public String getXmlResponse() {
    return xmlResponse;
  }

  public void setXmlResponse(String xmlResponse) {
    this.xmlResponse = xmlResponse;
  }

  public String getPdfResponse() {
    return pdfResponse;
  }

  public void setPdfResponse(String pdfResponse) {
    this.pdfResponse = pdfResponse;
  }

  public String getHtmlResponse() {
    return htmlResponse;
  }

  public void setHtmlResponse(String htmlResponse) {
    this.htmlResponse = htmlResponse;
  }

  @Override
  public String toString() {
    return "CibilReportBureauResponse [bureauName=" + bureauName + ", xmlResponse=" + xmlResponse
        + ", pdfResponse=" + pdfResponse + ", htmlResponse=" + htmlResponse + "]";
  }



}

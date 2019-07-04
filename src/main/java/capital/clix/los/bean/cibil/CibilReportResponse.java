package capital.clix.los.bean.cibil;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CibilReportResponse {

  private String resRefId;

  private String serviceName;

  private List<CibilReportBureauResponse> bureauResponse;

  private List<CibilReportBureauStatus> status;

  public String getResRefId() {
    return resRefId;
  }

  public void setResRefId(String resRefId) {
    this.resRefId = resRefId;
  }

  public String getServiceName() {
    return serviceName;
  }

  public void setServiceName(String serviceName) {
    this.serviceName = serviceName;
  }

  public List<CibilReportBureauResponse> getBureauResponse() {
    return bureauResponse;
  }

  public void setBureauResponse(List<CibilReportBureauResponse> bureauResponse) {
    this.bureauResponse = bureauResponse;
  }

  public List<CibilReportBureauStatus> getStatus() {
    return status;
  }

  public void setStatus(List<CibilReportBureauStatus> status) {
    this.status = status;
  }

  @Override
  public String toString() {
    return "CibilReportResponse [resRefId=" + resRefId + ", serviceName=" + serviceName
        + ", bureauResponse=" + bureauResponse + ", status=" + status + "]";
  }

}

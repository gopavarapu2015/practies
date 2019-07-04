package capital.clix.los.bean.cibil;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CibilReportBureauStatus {

  private String bureauName;
  private String type;
  private String remark;

  public String getBureauName() {
    return bureauName;
  }

  public void setBureauName(String bureauName) {
    this.bureauName = bureauName;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getRemark() {
    return remark;
  }

  public void setRemark(String remark) {
    this.remark = remark;
  }

  @Override
  public String toString() {
    return "CibilReportBureauStatus [bureauName=" + bureauName + ", type=" + type + ", remark="
        + remark + "]";
  }



}

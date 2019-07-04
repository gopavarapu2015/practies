package capital.clix.los.entity;

import java.util.Map;
import capital.clix.los.enums.AuditDataKey;

public class Audit {

  private String applicationId;

  private String type;

  private Map<AuditDataKey, String> auditData;

  public String getApplicationId() {
    return applicationId;
  }

  public void setApplicationId(String applicationId) {
    this.applicationId = applicationId;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Map<AuditDataKey, String> getAuditData() {
    return auditData;
  }

  public void setAuditData(Map<AuditDataKey, String> auditData) {
    this.auditData = auditData;
  }


}

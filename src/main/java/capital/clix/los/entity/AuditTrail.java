package capital.clix.los.entity;

import static capital.clix.los.constants.ApplicationConstant.COUCHBASE_KEY_SEPARATOR;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(value = Include.NON_NULL)
public class AuditTrail extends BaseEntity {

  private String applicationId;

  private List<Audit> auditList;

  public String getApplicationId() {
    return applicationId;
  }

  public void setApplicationId(String applicationId) {
    this.applicationId = applicationId;
  }

  public List<Audit> getAuditList() {
    return auditList;
  }

  public void setAuditList(List<Audit> auditList) {
    this.auditList = auditList;
  }

  @Override
  public void setId(String id) {
    this.id = AuditTrail.class.getSimpleName() + COUCHBASE_KEY_SEPARATOR + id;
  }

}

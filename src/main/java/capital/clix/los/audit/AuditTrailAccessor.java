package capital.clix.los.audit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import capital.clix.los.entity.Audit;
import capital.clix.los.entity.AuditTrail;
import capital.clix.los.enums.AuditDataKey;

public class AuditTrailAccessor {

  public static AuditTrail getAuditTrailEntity(Map<AuditDataKey, String> auditData,
      AuditTrail auditTrail) {


    Audit entity = new Audit();
    entity.setApplicationId(auditData.get(AuditDataKey.APPLICATION_ID));
    entity.setAuditData(auditData);

    if (auditTrail != null) {
      auditTrail.getAuditList().add(entity);

    } else {
      auditTrail = new AuditTrail();
      auditTrail.setId(auditData.get(AuditDataKey.APPLICATION_ID));
      auditTrail.setApplicationId(auditData.get(AuditDataKey.APPLICATION_ID));
      List<Audit> auditList = new ArrayList<Audit>();
      auditList.add(entity);
      auditTrail.setAuditList(auditList);
    }
    return auditTrail;
  }

}

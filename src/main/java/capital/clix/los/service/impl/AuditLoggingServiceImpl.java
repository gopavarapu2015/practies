package capital.clix.los.service.impl;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import capital.clix.los.couchbaseIntegration.ICouchBaseIntegration;
import capital.clix.los.enums.AuditDataKey;
import capital.clix.los.service.IAuditLoggingService;

@Component
public class AuditLoggingServiceImpl implements IAuditLoggingService {

  @Autowired
  @Qualifier("couchBaseIntegrationImpl")
  private ICouchBaseIntegration couchBaseEgestorImpl;


  @Override
  public void auditLogging(String loanApplicationId, String stage, String requestData) {

    if (!StringUtils.isBlank(loanApplicationId)) {

      Map<AuditDataKey, String> auditData = new HashMap<>();
      auditData.put(AuditDataKey.APPLICATION_ID, loanApplicationId);
      auditData.put(AuditDataKey.STAGE, stage);
      auditData.put(AuditDataKey.REQUEST_DATA, requestData);
      couchBaseEgestorImpl.saveAuditTrailEntity(auditData);

    }
  }


}

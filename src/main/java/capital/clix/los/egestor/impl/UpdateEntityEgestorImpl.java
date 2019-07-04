package capital.clix.los.egestor.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.query.N1qlQueryRow;
import capital.clix.cache.ConfigUtil;
import capital.clix.los.bean.AdditionalDetails;
import capital.clix.los.bean.BaseEntity;
import capital.clix.los.bean.BaseResponse;
import capital.clix.los.bean.CoApplicant;
import capital.clix.los.bean.pb.PBLoanEntity;
import capital.clix.los.commonUtility.CommonUtility;
import capital.clix.los.couchbaseIntegration.ICouchBaseIntegration;
import capital.clix.los.egestor.IEgestor;
import capital.clix.los.enums.AuditDataKey;
import capital.clix.los.enums.LoanType;
import capital.clix.los.enums.Property;

@Service("updateEntityEgestorImpl")
public class UpdateEntityEgestorImpl implements IEgestor {

  @Autowired
  @Qualifier("couchBaseIntegrationImpl")
  private ICouchBaseIntegration couchBaseEgestorImpl;

  private static final Logger LOG = LogManager.getLogger(UpdateEntityEgestorImpl.class);


  @Override
  public void preProcess(BaseEntity request, BaseResponse response) {
    LOG.info("Inside PreProcess to update Loan with ApplicationId:{}", request.getId());
    Map<AuditDataKey, String> auditData = new HashMap<>();
    auditData.put(AuditDataKey.APPLICATION_ID, request.getId());
    auditData.put(AuditDataKey.STAGE, "Loan Entity Update Pre Process");
    auditData.put(AuditDataKey.TX_UUID, request.getUuid());
    couchBaseEgestorImpl.saveAuditTrailEntity(auditData);

  }

  @Override
  public void process(BaseEntity request, BaseResponse response) {

    AdditionalDetails additionalDetail = (AdditionalDetails) request;
    String applicationId = request.getId();

    N1qlQueryRow row = couchBaseEgestorImpl.getEntityType(applicationId);
    JsonObject jsonObject = row.value();
    String type = jsonObject.getString("type");

    if (LoanType.SME_PB.getCode().equalsIgnoreCase(type)) {
      PBLoanEntity pbEntity =
          couchBaseEgestorImpl.getApplicationEntity(applicationId, PBLoanEntity.class);

      if (additionalDetail.getBankStatementRequest() != null) {
        List<Map<String, Object>> dataList =
            additionalDetail.getBankStatementRequest().getDocument().get("BANK_STATEMENT");

        for (Map<String, Object> map : dataList) {
          map.put("correlationId", CommonUtility.getTimeBasedUUID().toString());
          if (map.get("accountType").toString().equalsIgnoreCase("CASH_CREDIT")) {
            map.put("accountType", ConfigUtil.getPropertyValue(Property.CASH_CREDIT, String.class));
            map.put("account", "CASH_CREDIT");
          } else if (map.get("accountType").toString().equalsIgnoreCase("OVERDRAFT")) {
            map.put("accountType", ConfigUtil.getPropertyValue(Property.OVERDRAFT, String.class));
            map.put("account", "OVERDRAFT");
          } else if (map.get("accountType").toString().equalsIgnoreCase("CURRENT_ACCOUNT")) {
            map.put("accountType",
                ConfigUtil.getPropertyValue(Property.CURRENT_ACCOUNT, String.class));
            map.put("account", "CURRENT_ACCOUNT");
          }
          pbEntity.getDocument().putAll(additionalDetail.getBankStatementRequest().getDocument());

        }
      }
      List<CoApplicant> coAppList = additionalDetail.getCoApplicants();
      if (coAppList != null && coAppList.size() > 0) {

        String grpKey = CommonUtility.getTimeBasedUUID().toString();
        for (CoApplicant coApp : coAppList) {

          coApp.setGroupKey(grpKey);
          coApp.setId(CommonUtility.getTimeBasedUUID().toString());
          pbEntity.getCoApplicant().add(coApp);

        }

      }
      couchBaseEgestorImpl.updateLoanEntity(pbEntity);
    }
  }

  @Override
  public void postProcess(BaseEntity request, BaseResponse response) {
    LOG.info("Inside Post Process to update Loan with ApplicationId:{}", request.getId());
    Map<AuditDataKey, String> auditData = new HashMap<>();
    auditData.put(AuditDataKey.APPLICATION_ID, request.getId());
    auditData.put(AuditDataKey.STAGE, "Loan Entity Update Post Process");
    auditData.put(AuditDataKey.TX_UUID, request.getUuid());
    auditData.put(AuditDataKey.ERROR, response.getResponse());
    couchBaseEgestorImpl.saveAuditTrailEntity(auditData);

  }

}

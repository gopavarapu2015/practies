package capital.clix.los.egestor.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import capital.clix.cache.ConfigUtil;
import capital.clix.los.bean.BaseEntity;
import capital.clix.los.bean.BaseResponse;
import capital.clix.los.bean.bl.BankStatementRequest;
import capital.clix.los.bean.bl.BlLoanEntity;
import capital.clix.los.bean.laep.LAEPLoanEntity;
import capital.clix.los.commonUtility.CommonUtility;
import capital.clix.los.couchbaseIntegration.ICouchBaseIntegration;
import capital.clix.los.egestor.IEgestor;
import capital.clix.los.enums.AuditDataKey;
import capital.clix.los.enums.LoanType;
import capital.clix.los.enums.Property;

@Service("smeBankStatementUpdateEgestorImpl")
public class SmeBankStatementUpdateEgestorImpl implements IEgestor {

  @Autowired
  @Qualifier("couchBaseIntegrationImpl")
  private ICouchBaseIntegration couchBaseEgestorImpl;

  private static final Logger LOG = LogManager.getLogger(SmeBankStatementUpdateEgestorImpl.class);

  @Override
  public void preProcess(BaseEntity request, BaseResponse response) {
    BankStatementRequest bankStatementRequest = (BankStatementRequest) request;
    LOG.info("Inside Pre Process for updating Bank Statement with ApplicationId:{}",
        bankStatementRequest.getLoanApplicationId());

    Map<AuditDataKey, String> auditData = new HashMap<>();
    auditData.put(AuditDataKey.APPLICATION_ID, bankStatementRequest.getLoanApplicationId());
    auditData.put(AuditDataKey.STAGE, "Bank Statement Update");
    auditData.put(AuditDataKey.TX_UUID, request.getUuid());
    couchBaseEgestorImpl.saveAuditTrailEntity(auditData);

  }

  @Override
  public void process(BaseEntity request, BaseResponse response) {
    BankStatementRequest bankStatementRequest = (BankStatementRequest) request;

    try {
      if (LoanType.BL.getCode().equalsIgnoreCase(bankStatementRequest.getLoanType())) {
        BlLoanEntity entity = couchBaseEgestorImpl
            .getApplicationEntity(bankStatementRequest.getLoanApplicationId(), BlLoanEntity.class);

        LOG.info(
            "Inside Process for updating Bank Statement with ApplicationId:{},corelation_id:{},Statement:{}",
            bankStatementRequest.getLoanApplicationId(), request.getUuid(),
            bankStatementRequest.toString());

        List<Map<String, Object>> dataList =
            bankStatementRequest.getDocument().get("BANK_STATEMENT");

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
        }
        entity.getDocument().putAll(bankStatementRequest.getDocument());
        couchBaseEgestorImpl.updateLoanEntity(entity);

        LOG.info(
            "Inside Process After updating Bank Statement with ApplicationId:{},corelation_id:{},Statement:{}",
            bankStatementRequest.getLoanApplicationId(), request.getUuid(),
            bankStatementRequest.toString());
      }else if(LoanType.LAEP.getCode().equalsIgnoreCase(bankStatementRequest.getLoanType())){
    	  LAEPLoanEntity laepEntity = couchBaseEgestorImpl
    	            .getApplicationEntity(bankStatementRequest.getLoanApplicationId(), LAEPLoanEntity.class);
    	  
    	  LOG.info("Inside Process for updating Bank Statement for LAEP loan type with ApplicationId:{},corelation_id:{},Statement:{}",
    	            bankStatementRequest.getLoanApplicationId(), request.getUuid(),
    	            bankStatementRequest.toString());
    	  
    	  List<Map<String, Object>> dataList =
    	            bankStatementRequest.getDocument().get("BANK_STATEMENT");
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
            }
    	  
    	  laepEntity.getDocument().putAll(bankStatementRequest.getDocument());
          couchBaseEgestorImpl.updateLoanEntity(laepEntity);
          
          LOG.info("Inside Process After updating Bank Statement for LAEP loan type with ApplicationId:{},corelation_id:{},Statement:{}",
                  bankStatementRequest.getLoanApplicationId(), request.getUuid(),
                  bankStatementRequest.toString());
      }
    } catch (Exception e) {
      LOG.error(
          "Exception Occured Inside Process while updating Bank Statement with ApplicationId:{},corelation_id:{},Statement:{}",
          bankStatementRequest.getLoanApplicationId(), request.getUuid(), e);
    }

  }

  @Override
  public void postProcess(BaseEntity request, BaseResponse response) {
    BankStatementRequest bankStatementRequest = (BankStatementRequest) request;
    LOG.info("Inside Post Process After updating Bank Statement with ApplicationId:{}",
        bankStatementRequest.getLoanApplicationId());

    Map<AuditDataKey, String> auditData = new HashMap<>();
    auditData.put(AuditDataKey.APPLICATION_ID, bankStatementRequest.getLoanApplicationId());
    auditData.put(AuditDataKey.STAGE, "Bank Statement Update Successfully");
    auditData.put(AuditDataKey.TX_UUID, request.getUuid());
    couchBaseEgestorImpl.saveAuditTrailEntity(auditData);

  }
}

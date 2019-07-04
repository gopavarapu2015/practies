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

import capital.clix.los.bean.BaseEntity;
import capital.clix.los.bean.BaseResponse;
import capital.clix.los.bean.bl.BlLoanEntity;
import capital.clix.los.bean.fsa.FinancialStatementAnalysisRequest;
import capital.clix.los.couchbaseIntegration.ICouchBaseIntegration;
import capital.clix.los.egestor.IEgestor;
import capital.clix.los.enums.AuditDataKey;
import capital.clix.los.enums.LoanType;

@Service("financialStatementAnalysisUpdateEgestorImpl")
public class FinancialStatementAnalysisUpdateEgestorImpl implements IEgestor {
  @Autowired
  @Qualifier("couchBaseIntegrationImpl")
  private ICouchBaseIntegration couchBaseEgestorImpl;

  private static final Logger LOG =
      LogManager.getLogger(FinancialStatementAnalysisUpdateEgestorImpl.class);

  @Override
  public void preProcess(BaseEntity request, BaseResponse response) {
    FinancialStatementAnalysisRequest financialStatementAnalysisRequest =
        (FinancialStatementAnalysisRequest) request;
    LOG.info("Inside preProcess() for updating FSA with ApplicationId:{}",
        financialStatementAnalysisRequest.getLoanApplicationId());

    Map<AuditDataKey, String> auditData = new HashMap<>();
    auditData.put(AuditDataKey.APPLICATION_ID,
        financialStatementAnalysisRequest.getLoanApplicationId());
    auditData.put(AuditDataKey.STAGE, "FSA Update");
    auditData.put(AuditDataKey.TX_UUID, request.getUuid());
    couchBaseEgestorImpl.saveAuditTrailEntity(auditData);
    LOG.info("Audit Trail Entity Saved");

  }

  @Override
  public void process(BaseEntity request, BaseResponse response) {
    FinancialStatementAnalysisRequest financialStatementAnalysisRequest =
        (FinancialStatementAnalysisRequest) request;

    try {

      N1qlQueryRow row = couchBaseEgestorImpl.getEntityType(financialStatementAnalysisRequest.getLoanApplicationId());
      JsonObject jsonObject = row.value();
      String type = jsonObject.getString("type");
      if (LoanType.BL.getCode().equalsIgnoreCase(type)) {
        BlLoanEntity entity = couchBaseEgestorImpl.getApplicationEntity(
            financialStatementAnalysisRequest.getLoanApplicationId(), BlLoanEntity.class);

        LOG.info(
            "FSA Data recived for application ID::{} is ::" + financialStatementAnalysisRequest.getLoanApplicationId(),
            entity);

        LOG.info(
            "Inside Process for updating FSA with ApplicationId:{},corelation_id:{},Statement:{}",
            financialStatementAnalysisRequest.getLoanApplicationId(), request.getUuid(),
            financialStatementAnalysisRequest.toString());

        List<Map<String, Object>> dataList =
            financialStatementAnalysisRequest.getDocument().get("FINANCIAL_STATEMENT");

        entity.getDocument().putAll(financialStatementAnalysisRequest.getDocument());
        LOG.info("Updating Loan Entity for ApplicationID :: "+financialStatementAnalysisRequest.getLoanApplicationId());
        couchBaseEgestorImpl.updateLoanEntity(entity);
        LOG.info("LoanEntity Updated for ApplicationID :: "+financialStatementAnalysisRequest.getLoanApplicationId());

        LOG.info(
            "Inside Process After updating FSA with ApplicationId:{},corelation_id:{},Statement:{}",
            financialStatementAnalysisRequest.getLoanApplicationId(), request.getUuid(),
            financialStatementAnalysisRequest.toString());
      }
    } catch (Exception e) {
      LOG.error(
          "Exception Occured Inside Process while updating FSA with ApplicationId:{},corelation_id:{},Statement:{}",
          financialStatementAnalysisRequest.getLoanApplicationId(), request.getUuid(), e);
    }

  }

  @Override
  public void postProcess(BaseEntity request, BaseResponse response) {
    FinancialStatementAnalysisRequest financialStatementAnalysisRequest =
        (FinancialStatementAnalysisRequest) request;
    LOG.info("Inside Post Process After updating FSA with ApplicationId:{}",
        financialStatementAnalysisRequest.getLoanApplicationId());

    Map<AuditDataKey, String> auditData = new HashMap<>();
    auditData.put(AuditDataKey.APPLICATION_ID,
        financialStatementAnalysisRequest.getLoanApplicationId());
    auditData.put(AuditDataKey.STAGE, "FSA Update Successfully");
    auditData.put(AuditDataKey.TX_UUID, request.getUuid());
    couchBaseEgestorImpl.saveAuditTrailEntity(auditData);
    LOG.info("Inside PostProcess. AuditTrailEntity saved. ");

  }

}

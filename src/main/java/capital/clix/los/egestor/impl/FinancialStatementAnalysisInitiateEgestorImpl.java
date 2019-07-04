package capital.clix.los.egestor.impl;

import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import capital.clix.los.bean.BaseEntity;
import capital.clix.los.bean.BaseResponse;
import capital.clix.los.bean.WorkFlowRequestBean;
import capital.clix.los.bean.bl.BlLoanEntity;
import capital.clix.los.bean.fsa.FinancialStatementAnalysisRequest;
import capital.clix.los.couchbaseIntegration.ICouchBaseIntegration;
import capital.clix.los.egestor.IEgestor;
import capital.clix.los.enums.ApplicationStage;
import capital.clix.los.enums.AuditDataKey;
import capital.clix.los.enums.LoanType;
import capital.clix.los.webService.IWorkFlowEngineWebService;

@Service("financialStatementAnalysisInitiateEgestorImpl")
public class FinancialStatementAnalysisInitiateEgestorImpl implements IEgestor {
  @Autowired
  @Qualifier("workFlowEngineWebServiceImpl")
  private IWorkFlowEngineWebService workFlowEngineWebServiceImpl;

  @Autowired
  @Qualifier("couchBaseIntegrationImpl")
  private ICouchBaseIntegration couchBaseEgestorImpl;

  private static final Logger LOG =
      LogManager.getLogger(FinancialStatementAnalysisInitiateEgestorImpl.class);

  @Override
  public void preProcess(BaseEntity request, BaseResponse response) {
    FinancialStatementAnalysisRequest financialStatementAnalysisRequest =
        (FinancialStatementAnalysisRequest) request;
    Map<AuditDataKey, String> auditData = new HashMap<>();
    auditData.put(AuditDataKey.APPLICATION_ID,
        financialStatementAnalysisRequest.getLoanApplicationId());
    auditData.put(AuditDataKey.STAGE, "FSA Analysis Initiate Pre Process");
    auditData.put(AuditDataKey.TX_UUID, request.getUuid());
    couchBaseEgestorImpl.saveAuditTrailEntity(auditData);
    LOG.info(
        "Inside FinancialStatementAnalysisInitiateEgestorImpl class preProcess(). AuditTrailEntity Saved.");
  }

  @Override
  public void process(BaseEntity request, BaseResponse response) {
    FinancialStatementAnalysisRequest financialStatementAnalysisRequest =
        (FinancialStatementAnalysisRequest) request;
    try {
      WorkFlowRequestBean workFlowRequestBean = new WorkFlowRequestBean();
      workFlowRequestBean
          .setLoanApplicationId(financialStatementAnalysisRequest.getLoanApplicationId());
     
      BlLoanEntity entity = couchBaseEgestorImpl.getApplicationEntity(
              financialStatementAnalysisRequest.getLoanApplicationId(), BlLoanEntity.class);
	  workFlowRequestBean.setStage(entity.getStatus().getValue());
      workFlowRequestBean.setUuid(request.getUuid());
      LOG.info(
          "Inside Process for initiating FSA Analysis via WE for ApplicationId:{},WorkFlow Request:{},corelation_id:{}",
          financialStatementAnalysisRequest.getLoanApplicationId(), workFlowRequestBean.toString(),
          request.getUuid());
  
      if ("BL".equalsIgnoreCase(entity.getLoanType())) {
        workFlowRequestBean.setLoanType(LoanType.BL);
      }

      workFlowEngineWebServiceImpl.FinancialStatementAnalysisInitiate(workFlowRequestBean,
          response);
      LOG.info(
          "Inside Process, FSA Analysis Initiated Successfully via WE for ApplicationId:{},corelation_id:{}",
          financialStatementAnalysisRequest.getLoanApplicationId(), request.getUuid());
    } catch (Exception e) {

      LOG.error(
          "Exception Occured Inside Process for initiating FSA Analysis via WE for ApplicationId:{},corelation_id:{},Exception:{}",
          financialStatementAnalysisRequest.getLoanApplicationId(), request.getUuid(),
          e.getMessage());
      response.setResponse("En Exception Occured while initiating Sme FSA Analysis");
    }
  }

  @Override
  public void postProcess(BaseEntity request, BaseResponse response) {
    FinancialStatementAnalysisRequest financialStatementAnalysisRequest =
        (FinancialStatementAnalysisRequest) request;
    LOG.info("Inside Post Process After initiating FSA Analysis via WE fir ApplicationId:{}",
        financialStatementAnalysisRequest.getLoanApplicationId());
    Map<AuditDataKey, String> auditData = new HashMap<>();
    auditData.put(AuditDataKey.APPLICATION_ID,
        financialStatementAnalysisRequest.getLoanApplicationId());
    auditData.put(AuditDataKey.STAGE, "Inside Post Process FSA Analysis Initiated successfully");
    auditData.put(AuditDataKey.ERROR, response.getResponse());
    auditData.put(AuditDataKey.TX_UUID, request.getUuid());
    couchBaseEgestorImpl.saveAuditTrailEntity(auditData);
  }
}

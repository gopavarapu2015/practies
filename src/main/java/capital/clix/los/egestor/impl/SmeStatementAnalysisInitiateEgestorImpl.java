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
import capital.clix.los.bean.bl.BankStatementRequest;
import capital.clix.los.bean.bl.BlLoanEntity;
import capital.clix.los.couchbaseIntegration.ICouchBaseIntegration;
import capital.clix.los.egestor.IEgestor;
import capital.clix.los.enums.ApplicationStage;
import capital.clix.los.enums.AuditDataKey;
import capital.clix.los.enums.LoanType;
import capital.clix.los.webService.IWorkFlowEngineWebService;

@Service("smeStatementAnalysisInitiateEgestorImpl")
public class SmeStatementAnalysisInitiateEgestorImpl implements IEgestor {

  @Autowired
  @Qualifier("workFlowEngineWebServiceImpl")
  private IWorkFlowEngineWebService workFlowEngineWebServiceImpl;

  @Autowired
  @Qualifier("couchBaseIntegrationImpl")
  private ICouchBaseIntegration couchBaseEgestorImpl;

  private static final Logger LOG =
      LogManager.getLogger(SmeStatementAnalysisInitiateEgestorImpl.class);

  @Override
  public void preProcess(BaseEntity request, BaseResponse response) {
    BankStatementRequest bankStatementRequest = (BankStatementRequest) request;
    LOG.info("Inside Pre Process before initiating Statement Analysis via WE fir ApplicationId:{}",
        bankStatementRequest.getLoanApplicationId());
    Map<AuditDataKey, String> auditData = new HashMap<>();
    auditData.put(AuditDataKey.APPLICATION_ID, bankStatementRequest.getLoanApplicationId());
    auditData.put(AuditDataKey.STAGE, "Statement Analysis Initiate Pre Process");
    auditData.put(AuditDataKey.TX_UUID, request.getUuid());
    couchBaseEgestorImpl.saveAuditTrailEntity(auditData);
  }

  @Override
  public void process(BaseEntity request, BaseResponse response) {
    BankStatementRequest bankStatementRequest = (BankStatementRequest) request;
    try {
      WorkFlowRequestBean workFlowRequestBean = new WorkFlowRequestBean();
      workFlowRequestBean.setLoanApplicationId(bankStatementRequest.getLoanApplicationId());
      workFlowRequestBean.setUuid(request.getUuid());
      LOG.info(
          "Inside Process for initiating Statement Analysis via WE for ApplicationId:{},WorkFlow Request:{},corelation_id:{}",
          bankStatementRequest.getLoanApplicationId(), workFlowRequestBean.toString(),
          request.getUuid());
      
      BaseEntity entity = couchBaseEgestorImpl
              .getApplicationEntity(bankStatementRequest.getLoanApplicationId(), BaseEntity.class);
      if ("BL".equalsIgnoreCase(bankStatementRequest.getLoanType())) {
        workFlowRequestBean.setLoanType(LoanType.BL);
        workFlowRequestBean.setStage(ApplicationStage.STATEMENT_ANALYSIS_INITIATE.getValue());
      }
      else if("LAEP".equalsIgnoreCase(bankStatementRequest.getLoanType())){
    	  workFlowRequestBean.setLoanType(LoanType.LAEP);
          workFlowRequestBean.setStage(entity.getStatus().getValue());
      }
      workFlowEngineWebServiceImpl.SmeStatementAnalysisInitiate(workFlowRequestBean, response);
      LOG.info(
          "Inside Process, Statement Analysis Initiated Successfully via WE for ApplicationId:{},corelation_id:{}",
          bankStatementRequest.getLoanApplicationId(), request.getUuid());
    } catch (Exception e) {

      LOG.error(
          "Exception Occured Inside Process for initiating Statement Analysis via WE for ApplicationId:{},corelation_id:{},Exception:{}",
          bankStatementRequest.getLoanApplicationId(), request.getUuid(), e);
      response.setResponse("En Exception Occured while initiating Sme Statement Analysis");
    }
  }

  @Override
  public void postProcess(BaseEntity request, BaseResponse response) {
    BankStatementRequest bankStatementRequest = (BankStatementRequest) request;
    LOG.info("Inside Post Process After initiating Statement Analysis via WE fir ApplicationId:{}",
        bankStatementRequest.getLoanApplicationId());
    Map<AuditDataKey, String> auditData = new HashMap<>();
    auditData.put(AuditDataKey.APPLICATION_ID, bankStatementRequest.getLoanApplicationId());
    auditData.put(AuditDataKey.STAGE,
        "Inside Post Process Statement Analysis Initiated successfully");
    auditData.put(AuditDataKey.ERROR, response.getResponse());
    auditData.put(AuditDataKey.TX_UUID, request.getUuid());
    couchBaseEgestorImpl.saveAuditTrailEntity(auditData);
  }


}

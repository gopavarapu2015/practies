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
import capital.clix.los.bean.gst.GstStatementRequest;
import capital.clix.los.couchbaseIntegration.ICouchBaseIntegration;
import capital.clix.los.egestor.IEgestor;
import capital.clix.los.enums.ApplicationStage;
import capital.clix.los.enums.AuditDataKey;
import capital.clix.los.enums.LoanType;
import capital.clix.los.webService.IWorkFlowEngineWebService;

@Service("gstStatementAnalysisInitiateEgestorImpl")
public class GstStatementAnalysisInitiateEgestorImpl implements IEgestor {
  @Autowired
  @Qualifier("workFlowEngineWebServiceImpl")
  private IWorkFlowEngineWebService workFlowEngineWebServiceImpl;

  @Autowired
  @Qualifier("couchBaseIntegrationImpl")
  private ICouchBaseIntegration couchBaseEgestorImpl;

  private static final Logger LOG =
      LogManager.getLogger(GstStatementAnalysisInitiateEgestorImpl.class);

  @Override
  public void preProcess(BaseEntity request, BaseResponse response) {
    GstStatementRequest gstStatementRequest = (GstStatementRequest) request;
    LOG.info(
        "Inside Pre Process before initiating GST Statement Analysis via WE fir ApplicationId:{}",
        gstStatementRequest.getLoanApplicationId());
    Map<AuditDataKey, String> auditData = new HashMap<>();
    auditData.put(AuditDataKey.APPLICATION_ID, gstStatementRequest.getLoanApplicationId());
    auditData.put(AuditDataKey.STAGE, "GST Statement Analysis Initiate Pre Process");
    auditData.put(AuditDataKey.TX_UUID, request.getUuid());
    couchBaseEgestorImpl.saveAuditTrailEntity(auditData);
  }

  @Override
  public void process(BaseEntity request, BaseResponse response) {
    GstStatementRequest gstStatementRequest = (GstStatementRequest) request;
    try {
      WorkFlowRequestBean workFlowRequestBean = new WorkFlowRequestBean();
      workFlowRequestBean.setLoanApplicationId(gstStatementRequest.getLoanApplicationId());
      BlLoanEntity entity = couchBaseEgestorImpl.getApplicationEntity(
    		  gstStatementRequest.getLoanApplicationId(), BlLoanEntity.class);
      workFlowRequestBean.setStage(entity.getStatus().getValue());
      workFlowRequestBean.setUuid(request.getUuid());
      LOG.info(
          "Inside Process for initiating GST Statement Analysis via WE for ApplicationId:{},WorkFlow Request:{},corelation_id:{}",
          gstStatementRequest.getLoanApplicationId(), workFlowRequestBean.toString(),
          request.getUuid());
      if ("BL".equalsIgnoreCase(entity.getLoanType())) {
        workFlowRequestBean.setLoanType(LoanType.BL);
      }
     
      LOG.info("GST Analysis Initiated for applicationId :: "+gstStatementRequest.getLoanApplicationId());
      workFlowEngineWebServiceImpl.GstStatementAnalysisInitiate(workFlowRequestBean, response);
      LOG.info(
          "Inside Process, GST Statement Analysis Initiated Successfully via WE for ApplicationId:{},corelation_id:{}",
          gstStatementRequest.getLoanApplicationId(), request.getUuid());
    } catch (Exception e) {

      LOG.error(
          "Exception Occured Inside Process for initiating GST Statement Analysis via WE for ApplicationId:{},corelation_id:{},Exception:{}",
          gstStatementRequest.getLoanApplicationId(), request.getUuid(), e.getMessage());
      response.setResponse("En Exception Occured while initiating Sme GST Statement Analysis");
    }
  }

  @Override
  public void postProcess(BaseEntity request, BaseResponse response) {
    GstStatementRequest gstStatementRequest = (GstStatementRequest) request;
    LOG.info(
        "Inside Post Process After initiating GST Statement Analysis via WE fir ApplicationId:{}",
        gstStatementRequest.getLoanApplicationId());
    Map<AuditDataKey, String> auditData = new HashMap<>();
    auditData.put(AuditDataKey.APPLICATION_ID, gstStatementRequest.getLoanApplicationId());
    auditData.put(AuditDataKey.STAGE,
        "Inside Post Process GST Statement Analysis Initiated successfully");
    auditData.put(AuditDataKey.ERROR, response.getResponse());
    auditData.put(AuditDataKey.TX_UUID, request.getUuid());
    couchBaseEgestorImpl.saveAuditTrailEntity(auditData);
  }

}

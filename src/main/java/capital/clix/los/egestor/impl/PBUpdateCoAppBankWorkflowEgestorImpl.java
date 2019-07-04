package capital.clix.los.egestor.impl;

import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import com.google.gson.internal.LinkedTreeMap;
import capital.clix.los.bean.AdditionalDetails;
import capital.clix.los.bean.BaseEntity;
import capital.clix.los.bean.BaseResponse;
import capital.clix.los.bean.CoApplicant;
import capital.clix.los.bean.WorkFlowRequestBean;
import capital.clix.los.bean.pb.PBLoanEntity;
import capital.clix.los.couchbaseIntegration.ICouchBaseIntegration;
import capital.clix.los.egestor.IEgestor;
import capital.clix.los.enums.AuditDataKey;
import capital.clix.los.enums.ErrorState;
import capital.clix.los.enums.LoanType;
import capital.clix.los.exception.LosException;
import capital.clix.los.webService.IWorkFlowEngineWebService;

@Service("pBUpdateCoAppBankWorkflowEgestorImpl")
public class PBUpdateCoAppBankWorkflowEgestorImpl implements IEgestor {

  @Autowired
  @Qualifier("workFlowEngineWebServiceImpl")
  private IWorkFlowEngineWebService workFlowEngineWebServiceImpl;

  @Autowired
  @Qualifier("couchBaseIntegrationImpl")
  private ICouchBaseIntegration couchBaseEgestorImpl;

  private static final Logger LOG =
      LogManager.getLogger(PBUpdateCoAppBankWorkflowEgestorImpl.class);


  @Override
  public void preProcess(BaseEntity request, BaseResponse response) {
    Map<AuditDataKey, String> auditData = new HashMap<>();
    auditData.put(AuditDataKey.APPLICATION_ID, request.getId());
    auditData.put(AuditDataKey.STAGE, "PB Workflow Integration Initiated PreProcess");
    auditData.put(AuditDataKey.TX_UUID, request.getUuid());
    couchBaseEgestorImpl.saveAuditTrailEntity(auditData);

  }

  @Override
  public void process(BaseEntity request, BaseResponse response) {

    AdditionalDetails additionalDetail = (AdditionalDetails) request;
    try {
      WorkFlowRequestBean workFlowRequestBean = new WorkFlowRequestBean();
      workFlowRequestBean.setLoanApplicationId(request.getId());
      workFlowRequestBean.setLoanType(LoanType.SME_PB);
      LinkedTreeMap dataMap = new LinkedTreeMap<>();

      dataMap.put("coappsize", additionalDetail.getCoApplicants().size());


      PBLoanEntity entity =
          couchBaseEgestorImpl.getApplicationEntity(request.getId(), PBLoanEntity.class);

      CoApplicant coApp = entity.getCoApplicant().get(additionalDetail.getCoApplicants().size());
      dataMap.put("groupKey", coApp.getGroupKey());
      workFlowRequestBean.setTransientData(dataMap);
      workFlowRequestBean.setStage(entity.getStatus().toString());
      workFlowRequestBean.setUuid(request.getUuid());

      LOG.info(
          "Inside Process and calling Work Flow engine for PB update with applicationId:{}, WorkFlow Request:{}, uuid:{}",
          request.getId(), workFlowRequestBean.toString(), request.getUuid());
      workFlowEngineWebServiceImpl.initiateWorkflowEngine(workFlowRequestBean, response);
    } catch (Exception e) {
      LOG.error(
          "Exception Occured while calling PB update flow for applicationId:{}, exception is:{}, uuid:{}",
          request.getId(), e, request.getUuid());

      throw new LosException(ErrorState.INTERNAL_SERVER_ERROR, "Unable to call WE for PB update");
    }


  }

  @Override
  public void postProcess(BaseEntity request, BaseResponse response) {
    Map<AuditDataKey, String> auditData = new HashMap<>();
    auditData.put(AuditDataKey.APPLICATION_ID, request.getId());
    auditData.put(AuditDataKey.STAGE, "PB Workflow Integration Initiated Post Process");
    auditData.put(AuditDataKey.TX_UUID, request.getUuid());
    couchBaseEgestorImpl.saveAuditTrailEntity(auditData);

  }

}

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
import capital.clix.los.couchbaseIntegration.ICouchBaseIntegration;
import capital.clix.los.egestor.IEgestor;
import capital.clix.los.enums.ApplicationStage;
import capital.clix.los.enums.AuditDataKey;
import capital.clix.los.webService.IWorkFlowEngineWebService;

@Service("pbLoanBureauInitiateEgestorImpl")
public class PbLoanBureauInitiateEgestorImpl implements IEgestor {

  @Autowired
  @Qualifier("workFlowEngineWebServiceImpl")
  private IWorkFlowEngineWebService workFlowEngineWebServiceImpl;

  @Autowired
  @Qualifier("couchBaseIntegrationImpl")
  private ICouchBaseIntegration couchBaseEgestorImpl;

  private static final Logger LOG = LogManager.getLogger(PbLoanBureauInitiateEgestorImpl.class);


  @Override
  public void preProcess(BaseEntity request, BaseResponse response) {
    Map<AuditDataKey, String> auditData = new HashMap<>();
    auditData.put(AuditDataKey.APPLICATION_ID, request.getId().split(":")[1]);
    auditData.put(AuditDataKey.STAGE, "Bureau Process Initiated PreProcess");
    auditData.put(AuditDataKey.TX_UUID, request.getUuid());
    couchBaseEgestorImpl.saveAuditTrailEntity(auditData);

  }

  @Override
  public void process(BaseEntity request, BaseResponse response) {
    try {

      WorkFlowRequestBean workFlowRequestBean = new WorkFlowRequestBean();
      LOG.info("Application id:{},  Initiating Bureau Process ", request.getId().split(":")[1]);
      workFlowRequestBean.setLoanApplicationId(request.getId().split(":")[1]);
      workFlowRequestBean.setStage(ApplicationStage.PB_CREATED.getValue());
      workFlowRequestBean.setLoanType(request.getEntityType());
      workFlowRequestBean.setUuid(request.getUuid());
      LOG.info("ApplicationId:{}, Calling WE with Request:{}, uuid:{}",
          request.getId().split(":")[1], workFlowRequestBean.toString(), request.getUuid());
      workFlowEngineWebServiceImpl.MultiBureauCheck(workFlowRequestBean, response);
    } catch (Exception e) {
      LOG.error(
          "ApplicationId:{}, Exception occured while initiating bureau for PB with exception is:{}, uuid:{}",
          request.getId().split(":")[1], e, request.getUuid());
    }


  }

  @Override
  public void postProcess(BaseEntity request, BaseResponse response) {
    Map<AuditDataKey, String> auditData = new HashMap<>();
    auditData.put(AuditDataKey.APPLICATION_ID, request.getId().split(":")[1]);
    auditData.put(AuditDataKey.STAGE, "Bureau Process Initiated PostProcess");
    auditData.put(AuditDataKey.TX_UUID, request.getUuid());
    couchBaseEgestorImpl.saveAuditTrailEntity(auditData);

  }

}

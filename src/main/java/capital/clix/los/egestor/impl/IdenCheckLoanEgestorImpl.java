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
import capital.clix.los.enums.LoanType;
import capital.clix.los.webService.IWorkFlowEngineWebService;

@Service("idenCheckLoanEgestorImpl")
public class IdenCheckLoanEgestorImpl implements IEgestor {

  @Autowired
  @Qualifier("workFlowEngineWebServiceImpl")
  private IWorkFlowEngineWebService workFlowEngineWebServiceImpl;

  @Autowired
  @Qualifier("couchBaseIntegrationImpl")
  private ICouchBaseIntegration couchBaseEgestorImpl;

  private static final Logger LOG = LogManager.getLogger(IdenCheckLoanEgestorImpl.class);

  @Override
  public void preProcess(BaseEntity request, BaseResponse response) {

    Map<AuditDataKey, String> auditData = new HashMap<>();
    auditData.put(AuditDataKey.APPLICATION_ID, request.getId().split(":")[1]);
    auditData.put(AuditDataKey.STAGE, "Iden Check Initiated PreProcess");
    auditData.put(AuditDataKey.TX_UUID, request.getUuid());
    couchBaseEgestorImpl.saveAuditTrailEntity(auditData);
  }

  @Override
  public void process(BaseEntity request, BaseResponse response) {

    try {

      WorkFlowRequestBean workFlowRequestBean = new WorkFlowRequestBean();
      LOG.info("Inside Process method for multi bureau consumer Check with Application id:{}",
          request.getId().split(":")[1]);
      BaseEntity entity = couchBaseEgestorImpl.getEntityById(
              request.getId(), BaseEntity.class);
      workFlowRequestBean.setLoanApplicationId(request.getId().split(":")[1]);
      workFlowRequestBean.setStage(entity.getStatus().getValue());
      workFlowRequestBean.setLoanType(request.getEntityType());
      workFlowRequestBean.setUuid(request.getUuid());
      LOG.info(
          "Inside Process of Iden Check and calling Work Flow engine for applicationId:{}, WorkFlow Request:{}, uuid:{}",
          request.getId().split(":")[1], workFlowRequestBean.toString(), request.getUuid());
      workFlowEngineWebServiceImpl.MultiBureauCheck(workFlowRequestBean, response);
    } catch (Exception e) {
      LOG.error(
          "Exception Occured while doing Iden Check for applicationId:{}, exception is:{}, uuid:{}",
          request.getId().split(":")[1], e, request.getUuid());
    }

  }

  @Override
  public void postProcess(BaseEntity request, BaseResponse response) {
    Map<AuditDataKey, String> auditData = new HashMap<>();
    auditData.put(AuditDataKey.APPLICATION_ID, request.getId().split(":")[1]);
    auditData.put(AuditDataKey.STAGE, "Iden Check Initiated PostProcess");
    auditData.put(AuditDataKey.TX_UUID, request.getUuid());
    couchBaseEgestorImpl.saveAuditTrailEntity(auditData);

  }

}

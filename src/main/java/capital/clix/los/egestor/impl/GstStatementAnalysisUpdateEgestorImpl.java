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
import capital.clix.los.bean.gst.GstStatementRequest;
import capital.clix.los.couchbaseIntegration.ICouchBaseIntegration;
import capital.clix.los.egestor.IEgestor;
import capital.clix.los.enums.AuditDataKey;
import capital.clix.los.enums.LoanType;

@Service("gstStatementAnalysisUpdateEgestorImpl")
public class GstStatementAnalysisUpdateEgestorImpl implements IEgestor {
  @Autowired
  @Qualifier("couchBaseIntegrationImpl")
  private ICouchBaseIntegration couchBaseEgestorImpl;

  private static final Logger LOG = LogManager.getLogger(SmeBankStatementUpdateEgestorImpl.class);

  @Override
  public void preProcess(BaseEntity request, BaseResponse response) {
    GstStatementRequest gstStatementRequest = (GstStatementRequest) request;
    LOG.info("Inside preProcess for updating GST Statement with ApplicationId:{}",
        gstStatementRequest.getLoanApplicationId());

    Map<AuditDataKey, String> auditData = new HashMap<>();
    auditData.put(AuditDataKey.APPLICATION_ID, gstStatementRequest.getLoanApplicationId());
    auditData.put(AuditDataKey.STAGE, "GST Statement Update");
    auditData.put(AuditDataKey.TX_UUID, request.getUuid());
    couchBaseEgestorImpl.saveAuditTrailEntity(auditData);

  }

  @Override
  public void process(BaseEntity request, BaseResponse response) {
    GstStatementRequest gstStatementRequest = (GstStatementRequest) request;

    LOG.info("GstStatementRequest data is : " + gstStatementRequest.toString());
    
    N1qlQueryRow row = couchBaseEgestorImpl.getEntityType(gstStatementRequest.getLoanApplicationId());
    JsonObject jsonObject = row.value();
    String type = jsonObject.getString("type");

    try {
      if (LoanType.BL.getCode().equalsIgnoreCase(type)) {
        BlLoanEntity entity = couchBaseEgestorImpl
            .getApplicationEntity(gstStatementRequest.getLoanApplicationId(), BlLoanEntity.class);

        LOG.info("Loan Application Data : " + entity.toString());

        LOG.info(
            "Inside Process for updating GST Statement with ApplicationId:{},corelation_id:{},Statement:{}",
            gstStatementRequest.getLoanApplicationId(), request.getUuid(),
            gstStatementRequest.toString());

        List<Map<String, Object>> dataList = gstStatementRequest.getDocument().get("GST_STATEMENT");


        entity.getDocument().putAll(gstStatementRequest.getDocument());
        LOG.info("Started Updating Loan Entity for ApplicationId ::"+gstStatementRequest.getLoanApplicationId());
        couchBaseEgestorImpl.updateLoanEntity(entity);
        LOG.info("Loan Entity updated for ApplicationId :: "+gstStatementRequest.getLoanApplicationId());

        LOG.info(
            "Inside Process After updating Gst Statement with ApplicationId:{},corelation_id:{},Statement:{}",
            gstStatementRequest.getLoanApplicationId(), request.getUuid(),
            gstStatementRequest.toString());
      }
    } catch (Exception e) {
      LOG.error(
          "Exception Occured Inside Process while updating Gst Statement with ApplicationId:{},corelation_id:{},Statement:{}",
          gstStatementRequest.getLoanApplicationId(), request.getUuid(), e);
    }

  }

  @Override
  public void postProcess(BaseEntity request, BaseResponse response) {
    GstStatementRequest gstStatementRequest = (GstStatementRequest) request;
    LOG.info("Inside Post Process After updating Gst Statement with ApplicationId:{}",
        gstStatementRequest.getLoanApplicationId());

    Map<AuditDataKey, String> auditData = new HashMap<>();
    auditData.put(AuditDataKey.APPLICATION_ID, gstStatementRequest.getLoanApplicationId());
    auditData.put(AuditDataKey.STAGE, "GST Statement Update Successfully");
    auditData.put(AuditDataKey.TX_UUID, request.getUuid());
    couchBaseEgestorImpl.saveAuditTrailEntity(auditData);

  }
}

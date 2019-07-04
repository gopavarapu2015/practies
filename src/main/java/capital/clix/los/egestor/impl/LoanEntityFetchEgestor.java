package capital.clix.los.egestor.impl;

import java.util.HashMap;
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
import capital.clix.los.bean.PersonalLoanEntity;
import capital.clix.los.bean.bl.BlLoanEntity;
import capital.clix.los.bean.laep.LAEPLoanEntity;
import capital.clix.los.bean.pb.PBLoanEntity;
import capital.clix.los.couchbaseIntegration.ICouchBaseIntegration;
import capital.clix.los.egestor.IEgestor;
import capital.clix.los.enums.AuditDataKey;
import capital.clix.los.enums.LoanType;
import capital.clix.los.enums.Portfolio;

@Service("loanEntityFetchEgestor")
public class LoanEntityFetchEgestor implements IEgestor {

  @Autowired
  @Qualifier("couchBaseIntegrationImpl")
  private ICouchBaseIntegration couchBaseEgestorImpl;

  private static final Logger LOG = LogManager.getLogger(LoanEntityFetchEgestor.class);

  private void setResponse(BaseEntity request, BaseResponse response) {
    if (request != null) {
      request.setId(request.getId().split(":")[1]);
      response.setLosApplicationData(request);
      response.setSuccess(true);
      response.setApplicationId(request.getId());
    }

  }

  @Override
  public void preProcess(BaseEntity request, BaseResponse response) {

    LOG.info("Inside PreProcess to Fetch Loan with ApplicationId:{}", request.getId());
    Map<AuditDataKey, String> auditData = new HashMap<>();
    auditData.put(AuditDataKey.APPLICATION_ID, request.getId());
    auditData.put(AuditDataKey.STAGE, "Loan Entity Fetch Pre Process");
    auditData.put(AuditDataKey.TX_UUID, request.getUuid());
    couchBaseEgestorImpl.saveAuditTrailEntity(auditData);

  }

  @Override
  public void process(BaseEntity request, BaseResponse response) {
    try {
      N1qlQueryRow row = couchBaseEgestorImpl.getEntityType(request.getId().toString());
      JsonObject jsonObject = row.value();
      String type = jsonObject.getString("type");
      LOG.info("Inside Process to Fetch Loan with ApplicationId:{}, type is:{}, corelation_id:{}",
          request.getId(), type, request.getUuid());
      if (Portfolio.BL.getCode().equalsIgnoreCase(type)) {
        request = couchBaseEgestorImpl.getApplicationEntity(request.getId().toString(),
            BlLoanEntity.class);

        setResponse(request, response);
      } else if (Portfolio.PL.getCode().equalsIgnoreCase(type)) {

        request = couchBaseEgestorImpl.getApplicationEntity(request.getId().toString(),
            PersonalLoanEntity.class);
        setResponse(request, response);
      } else if (LoanType.SME_PB.name().equalsIgnoreCase(type)) {

        request = couchBaseEgestorImpl.getApplicationEntity(request.getId().toString(),
            PBLoanEntity.class);
        setResponse(request, response);
      }else if (LoanType.LAEP.name().equalsIgnoreCase(type)) {

          request = couchBaseEgestorImpl.getApplicationEntity(request.getId().toString(),
              LAEPLoanEntity.class);
          setResponse(request, response);
        }

    } catch (Exception e) {
      LOG.error(
          "Exception occured while fetching Loan with ApplicationId:{}, exception:{}, corelation_id:{}",
          request.getId(), e, request.getUuid());
      response.setResponse("Error Occured during Fetch is:-" + e.getMessage());
    }

  }

  @Override
  public void postProcess(BaseEntity request, BaseResponse response) {

    LOG.info("Inside PostProcess to Fetch Loan with ApplicationId:{}", request.getId());
    Map<AuditDataKey, String> auditData = new HashMap<>();
    auditData.put(AuditDataKey.APPLICATION_ID, request.getId());
    auditData.put(AuditDataKey.STAGE, "Loan Entity Fetch Post Process");
    auditData.put(AuditDataKey.TX_UUID, request.getUuid());
    auditData.put(AuditDataKey.ERROR, response.getResponse());
    couchBaseEgestorImpl.saveAuditTrailEntity(auditData);

  }
}

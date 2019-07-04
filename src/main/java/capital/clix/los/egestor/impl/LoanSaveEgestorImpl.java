package capital.clix.los.egestor.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import capital.clix.cache.ConfigUtil;
import capital.clix.los.bean.BaseEntity;
import capital.clix.los.bean.BaseResponse;
import capital.clix.los.bean.CoApplicant;
import capital.clix.los.bean.PersonalLoanEntity;
import capital.clix.los.bean.bl.BlLoanEntity;
import capital.clix.los.bean.laep.LAEPLoanEntity;
import capital.clix.los.bean.pb.PBLoanEntity;
import capital.clix.los.commonUtility.CommonUtility;
import capital.clix.los.constants.ApplicationConstant;
import capital.clix.los.couchbaseIntegration.ICouchBaseIntegration;
import capital.clix.los.egestor.IEgestor;
import capital.clix.los.enums.ApplicationStage;
import capital.clix.los.enums.AuditDataKey;
import capital.clix.los.enums.LoanType;
import capital.clix.los.enums.Prefix;
import capital.clix.los.enums.Property;

@Service("loanSaveEgestorImpl")
public class LoanSaveEgestorImpl implements IEgestor {

  @Autowired
  @Qualifier("couchBaseIntegrationImpl")
  private ICouchBaseIntegration couchBaseEgestorImpl;

  private static final Logger LOG = LogManager.getLogger(LoanSaveEgestorImpl.class);
  DateFormat df = new SimpleDateFormat(ApplicationConstant.SME_DATE_FORMET);

  @Override
  public void preProcess(BaseEntity request, BaseResponse response) {

    LOG.info(" Inside Preprocess  method ");
  }

  @Override
  public void process(BaseEntity request, BaseResponse response) {
    LoanType loanType = request.getEntityType();

    try {
      if (LoanType.PL == loanType) {
        PersonalLoanEntity plEntity = (PersonalLoanEntity) request;
        List<CoApplicant> coApplicant = plEntity.getCoApplicant();
        if (coApplicant != null && !coApplicant.isEmpty()) {

          coApplicant.forEach(coApplic -> {

            UUID uuid = CommonUtility.getTimeBasedUUID();
            coApplic.setId(uuid.toString());
          });

        }
        UUID uuid = CommonUtility.getTimeBasedUUID();
        request.setId(uuid.toString());

        couchBaseEgestorImpl.saveLoanEntity(plEntity, response);
        response.setApplicationId(uuid.toString());
      } else if (LoanType.BL == loanType) {


        BlLoanEntity blEntity = (BlLoanEntity) request;

        LOG.info("Inside Process to save BL Loan Entity:{}", blEntity.toString());

        blEntity.setStatus(ApplicationStage.CREATED);


        blEntity.setCreated(new Date());
        blEntity.setUpdated(new Date());
        blEntity.setSource("CLIX");
        List<CoApplicant> coApplicant = blEntity.getCoApplicant();
        if (coApplicant != null && !coApplicant.isEmpty()) {

          coApplicant.forEach(coApplic -> {

            UUID uuid = CommonUtility.getTimeBasedUUID();
            coApplic.setId(uuid.toString());

          });
          UUID uuid = CommonUtility.getTimeBasedUUID();
          blEntity.setId(Prefix.APPLICATION.getCode() + uuid.toString());

          couchBaseEgestorImpl.saveLoanEntity(blEntity, response);
          response.setApplicationId(uuid.toString());

        }
      } 
      else if (LoanType.LAEP == loanType) {


          LAEPLoanEntity laepEntity = (LAEPLoanEntity) request;

          LOG.info("Inside Process to save LAEP Loan Entity:{}", laepEntity.toString());

          laepEntity.setStatus(ApplicationStage.LAEP_CREATED);


          laepEntity.setCreated(new Date());
          laepEntity.setUpdated(new Date());
          laepEntity.setSource("CLIX");
          List<CoApplicant> coApplicant = laepEntity.getCoApplicant();
          if (coApplicant != null && !coApplicant.isEmpty()) {

            coApplicant.forEach(coApplic -> {

              UUID uuid = CommonUtility.getTimeBasedUUID();
              String appId = "LP-"+uuid.toString();
              coApplic.setId(appId);

            });
            UUID uuid = CommonUtility.getTimeBasedUUID();
            String appId = "LP-"+uuid.toString();
            laepEntity.setId(Prefix.APPLICATION.getCode() + appId);
            System.out.println("hhhh "+Prefix.APPLICATION.getCode() + appId);
            System.out.println("Application Id is :: "+laepEntity.getId());

            couchBaseEgestorImpl.saveLoanEntity(laepEntity, response);
            response.setApplicationId(appId);

          }
        }
      else if (LoanType.SME_PB == loanType) {

        createPBLoan(request, response);
      }

    } catch (Exception e) {
      LOG.error("Exception occured while Saving Entity:{},with Exception:{}", request.toString(),
          e);
      response.setResponse("Exception occured while saving Loan Entity");
    }
  }

  @Override
  public void postProcess(BaseEntity request, BaseResponse response) {
    Map<AuditDataKey, String> auditData = new HashMap<>();

    LoanType loanType = request.getEntityType();
    if (LoanType.BL == loanType) {

      BlLoanEntity blEntity = (BlLoanEntity) request;
      LOG.info("Inside Post Process After Saving BL entity with generated applicationId:{}",
          blEntity.getId());
      auditData.put(AuditDataKey.APPLICATION_ID, blEntity.getId().split(":")[1]);
      auditData.put(AuditDataKey.STAGE, "BL Loan Entity Saved  Post Process");
      auditData.put(AuditDataKey.TX_UUID, blEntity.getUuid());
      auditData.put(AuditDataKey.ERROR, response.getResponse());
      couchBaseEgestorImpl.saveAuditTrailEntity(auditData);
    }
    if (LoanType.LAEP == loanType) {

        LAEPLoanEntity blEntity = (LAEPLoanEntity) request;
        LOG.info("Inside Post Process After Saving LAEP entity with generated applicationId:{}",
            blEntity.getId());
        auditData.put(AuditDataKey.APPLICATION_ID, blEntity.getId().split(":")[1]);
        auditData.put(AuditDataKey.STAGE, "BL Loan Entity Saved  Post Process");
        auditData.put(AuditDataKey.TX_UUID, blEntity.getUuid());
        auditData.put(AuditDataKey.ERROR, response.getResponse());
        couchBaseEgestorImpl.saveAuditTrailEntity(auditData);
      }
    
  }

  private void createPBLoan(BaseEntity request, BaseResponse response) {
    PBLoanEntity pbEntity = (PBLoanEntity) request;

    String applicationId = Prefix.PB.getCode() + new Date().getTime();

    LOG.info("Saving Paisa Bazaar Loan Entity:{}, with key:{}", pbEntity.toString(), applicationId);

    pbEntity.setStatus(ApplicationStage.PB_CREATED);

    ZoneId zoneid1 = ZoneId.of(ConfigUtil.getPropertyValue(Property.PB_ZONE, String.class));

    LocalDateTime id1 = LocalDateTime.now(zoneid1);

    ZonedDateTime zdt = id1.atZone(ZoneId.systemDefault());

    Date date = Date.from(zdt.toInstant());

    pbEntity.setCreated(date);
    pbEntity.setUpdated(date);
    pbEntity.setCreatedDateInMilliSec(date.getTime());

    pbEntity.setSource(ConfigUtil.getPropertyValue(Property.PB_SOURCE, String.class));

    List<CoApplicant> coApplicant = pbEntity.getCoApplicant();
    pbEntity.setId(Prefix.APPLICATION.getCode() + applicationId);
    if (coApplicant != null && !coApplicant.isEmpty()) {

      coApplicant.forEach(coApplic -> {

        String coApplicantId = Prefix.PB.getCode() + new Date().getTime();
        coApplic.setId(coApplicantId);

      });

    }
    couchBaseEgestorImpl.saveLoanEntity(pbEntity, response);
    response.setApplicationId(applicationId);
  }
}

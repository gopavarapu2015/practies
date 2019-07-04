package capital.clix.los.stageupdate.impl;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.couchbase.core.CouchbaseTemplate;
import org.springframework.stereotype.Service;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.query.N1qlQueryRow;
import capital.clix.los.bean.LosSyncDto;
import capital.clix.los.bean.bl.BlLoanEntity;
import capital.clix.los.couchbaseIntegration.ICouchBaseIntegration;
import capital.clix.los.enums.ApplicationStage;
import capital.clix.los.enums.LosStatus;
import capital.clix.los.enums.Portfolio;
import capital.clix.los.enums.Prefix;
import capital.clix.los.enums.WebNotificationDescription;
import capital.clix.los.stageupdate.IStageUpdate;
import capital.clix.los.webService.IWebNotificationService;
import capital.clix.los.webService.impl.PushNotificationFactory;
@Service("fsaStageUpdate")
public class FSAStageUpdate implements IStageUpdate {
	 @Autowired
	  private CouchbaseTemplate template;

	  @Autowired
	  @Qualifier("couchBaseIntegrationImpl")
	  private ICouchBaseIntegration couchBaseIntegrationImpl;
	  
	  @Autowired
	  @Qualifier("pushNotificationFactory")
	  PushNotificationFactory pushNotificationFactory;
	  
	  private static final Logger LOG = LogManager.getLogger(BureauStageUpdate.class);
	  
	  @Override
	  public void updateApplicationStage(LosSyncDto losSyncDto, String applicationId,
	      LosStatus losStatus) {

	    if (losSyncDto.getErrorResponseDto() != null) {
	      LOG.info(" Sending FSA statement Error notification for application Id :{}, and stage Data : {}",
		        applicationId, losSyncDto.toString());
	      updateApplicationStatus(Prefix.APPLICATION.getCode() + losSyncDto.getApplicationId(),
	          ApplicationStage.FSA_INITIATION_FAILED);

	      sendWebNotification(losSyncDto, losStatus, WebNotificationDescription.FSA_FAIL);
	      return;
	    }
	    switch (losStatus) {
	      case FSA_ANALYSIS_INITIATED: {

	        if (losSyncDto.getContent() != null && !losSyncDto.getContent().isEmpty()) {
	        	//saveFSAResponse(losSyncDto, applicationId,losStatus);
	        	updateApplicationStatus(Prefix.APPLICATION.getCode() + losSyncDto.getApplicationId(),
	      	          ApplicationStage.FSA_ANALYSIS_INITIATE);
	          sendWebNotification(losSyncDto, losStatus,
	              WebNotificationDescription.FSA_ANALYSIS_INITIATE);

	        }
	        break;
	      }
	    }

	  }

	/*
	 * private void saveFSAResponse(LosSyncDto losSyncDto, String applicationId,
	 * LosStatus losStatus) {
	 * LOG.info("Updating the stage :{},for application id:{}",
	 * ApplicationStage.FSA_ANALYSIS_INITIATE, losSyncDto.getApplicationId());
	 * SmeGSTResponse SmeGSTResponse=new SmeGSTResponse();
	 * System.out.println(Prefix.GST.getCode()+applicationId);
	 * 
	 * String str =Prefix.GST.getCode()+applicationId; BaseEntity request=new
	 * BaseEntity(); SmeGSTResponse.setId(str);
	 * 
	 * LinkedTreeMap<String,String> tm= new LinkedTreeMap<>(); Gson g = new Gson();
	 * String str1 = g.toJson(losSyncDto.getContent()); tm.put("GSTResponse",str1);
	 * SmeGSTResponse.setApplicantReports(tm); System.out.println(); BaseResponse
	 * response=new BaseResponse();
	 * couchBaseIntegrationImpl.saveLoanEntity(SmeGSTResponse, response);
	 * 
	 * }
	 */
	  private void updateApplicationStatus(String applicationId, ApplicationStage stage) {
		    if (applicationId.contains(":")) {
		      applicationId = applicationId.split(":")[1];
		    }
		    N1qlQueryRow row = couchBaseIntegrationImpl.getEntityType(applicationId);
		    JsonObject jsonObject = row.value();
		    String type = jsonObject.getString("type");
		    if (Portfolio.BL.getCode().equalsIgnoreCase(type)) {
		      BlLoanEntity blLoanEntity =
		          couchBaseIntegrationImpl.getApplicationEntity(applicationId, BlLoanEntity.class);
		      blLoanEntity.setStatus(stage);
		      couchBaseIntegrationImpl.updateLoanEntity(blLoanEntity);
		    }

		  }
	  private void sendWebNotification(LosSyncDto losSyncDto, LosStatus losStatus,
		      WebNotificationDescription webNotificationDescription) {

		    N1qlQueryRow row = couchBaseIntegrationImpl
		        .getSingleField(Prefix.APPLICATION.getCode() + losSyncDto.getApplicationId(), "source");
		    JsonObject jsonObject = row.value();
		    String source = jsonObject.getString("source");
		    IWebNotificationService webNotificationServiceImpl =
		        pushNotificationFactory.getNotificationInstance(source);
		    webNotificationServiceImpl.sendWebNotification(losSyncDto, source, losStatus,
		        webNotificationDescription);

		  }
	@Override
	public void sendErrorNotification(LosSyncDto losSyncDto, WebNotificationDescription webNotificationDescription) {
		// TODO Auto-generated method stub
		
	}
}

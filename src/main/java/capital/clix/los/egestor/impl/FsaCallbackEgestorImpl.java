package capital.clix.los.egestor.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.query.N1qlQueryRow;
import com.google.gson.internal.LinkedTreeMap;

import capital.clix.los.bean.BaseEntity;
import capital.clix.los.bean.BaseResponse;
import capital.clix.los.bean.CoApplicant;
import capital.clix.los.bean.LosSyncDto;
import capital.clix.los.bean.bl.BlLoanEntity;
import capital.clix.los.bean.bl.SmePerfiosReport;
import capital.clix.los.bean.fsa.SmeFsaReport;
import capital.clix.los.commonUtility.CommonUtility;
import capital.clix.los.couchbaseIntegration.ICouchBaseIntegration;
import capital.clix.los.egestor.IEgestor;
import capital.clix.los.enums.ApplicationStage;
import capital.clix.los.enums.AuditDataKey;
import capital.clix.los.enums.ErrorState;
import capital.clix.los.enums.LosStatus;
import capital.clix.los.enums.Prefix;
import capital.clix.los.enums.WebNotificationDescription;
import capital.clix.los.exception.LosException;
import capital.clix.los.webService.IWebNotificationService;
import capital.clix.los.webService.impl.PushNotificationFactory;

@Service("fsaCallbackEgestorImpl")
public class FsaCallbackEgestorImpl implements IEgestor {
	@Autowired
	@Qualifier("couchBaseIntegrationImpl")
	private ICouchBaseIntegration couchBaseIntegrationImpl;

	  @Autowired
	  @Qualifier("pushNotificationFactory")
	  PushNotificationFactory pushNotificationFactory;
	
	@Autowired
	@Qualifier("clixWebNotificationServiceImpl")
	private IWebNotificationService clixWebNotificationServiceImpl;

	private static final Logger LOG = LogManager.getLogger(FsaCallbackEgestorImpl.class);

	@Override
	public void preProcess(BaseEntity request, BaseResponse response) {

		SmeFsaReport smeFsaReport = (SmeFsaReport) request;
		LOG.info("Inside PreProcess for FSA Callback with ApplicationId:{}", request.getId());
		Map<AuditDataKey, String> auditData = new HashMap<>();
		auditData.put(AuditDataKey.APPLICATION_ID, smeFsaReport.getLoanApplicationId());
		auditData.put(AuditDataKey.STAGE, "FSA Callback Pre Process");
		auditData.put(AuditDataKey.TX_UUID, request.getUuid());
		couchBaseIntegrationImpl.saveAuditTrailEntity(auditData);
		LOG.info("Inside FsaCallbackEgestorImpl class preProcess(). AuditTrailEntity Saved.");
	}

	@Override
	public void process(BaseEntity request, BaseResponse response) {

		SmeFsaReport smeFsaReport = (SmeFsaReport) request;
		if (!smeFsaReport.getSuccess()) {
			LOG.info("Got success as false so calling  sendFsaAnalysisErrorNotification()");
			
			BlLoanEntity entity = couchBaseIntegrationImpl
		            .getApplicationEntity(smeFsaReport.getLoanApplicationId(), BlLoanEntity.class);
		
			entity.setStatus(ApplicationStage.FSA_ANALYSIS_FAILED);
			couchBaseIntegrationImpl.updateLoanEntity(entity);
			
			LinkedTreeMap report = smeFsaReport.getApplicantReports();

			LOG.info(Prefix.FSA.getCode()+smeFsaReport.getLoanApplicationId());
			
			String str =Prefix.FSA.getCode()+smeFsaReport.getLoanApplicationId();
			
	        request.setId(str);
			if (smeFsaReport != null) {

				smeFsaReport.setApplicantReports(report);
				couchBaseIntegrationImpl.saveLoanEntity(smeFsaReport, response);
				response.setApplicationId(str);

				LOG.info(
						"FSA Report saved successfully and stage updated for application id{},corelation_id:{}",
						smeFsaReport.getLoanApplicationId(), smeFsaReport.getUuid());
			}
			
			sendFsaAnalysisErrorNotification(smeFsaReport);

			throw new LosException(ErrorState.INTERNAL_SERVER_ERROR,
					"FSA Analysis Failed and Received report is Blank with status false");
		}

		try {

			LOG.info("Inside Process for Saving FSA Report for corelation_id:{},data:{}",
					smeFsaReport.getUuid(), smeFsaReport.toString());

			LinkedTreeMap report = smeFsaReport.getApplicantReports();

			LOG.info(Prefix.FSA.getCode()+smeFsaReport.getLoanApplicationId());
			
			String str =Prefix.FSA.getCode()+smeFsaReport.getLoanApplicationId();
			
	        request.setId(str);
			if (smeFsaReport != null) {

				smeFsaReport.setApplicantReports(report);
				couchBaseIntegrationImpl.saveLoanEntity(smeFsaReport, response);
				response.setApplicationId(str);

				LOG.info(
						"FSA Report saved successfully and stage updated for application id{},corelation_id:{}",
						smeFsaReport.getLoanApplicationId(), smeFsaReport.getUuid());
			}
			response.setResponse("FSA Called Successfully");
			response.setSuccess(true);
			
			BlLoanEntity entity = couchBaseIntegrationImpl
			            .getApplicationEntity(smeFsaReport.getLoanApplicationId(), BlLoanEntity.class);
			
			entity.setStatus(ApplicationStage.FSA_ANALYSIS_COMPLETED);
			couchBaseIntegrationImpl.updateLoanEntity(entity);
			LosSyncDto losSyncDto=new LosSyncDto();
			losSyncDto.setApplicationId(smeFsaReport.getLoanApplicationId());
			LOG.info("Calling sendWebNotification()");
			sendWebNotification(losSyncDto,LosStatus.FSA_COMPLETE, WebNotificationDescription.FSA_ANALYSIS_COMPLETED);
			LOG.info("WebNotification Complted");
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(" Exception occured while saving FSA report for application id: {} is :{}",
					smeFsaReport.getLoanApplicationId(), e);
			throw new LosException(ErrorState.INTERNAL_SERVER_ERROR,
					"Exception Occured while saving FSA report");

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
	private void sendFsaAnalysisErrorNotification(SmeFsaReport smeFsaReport) {
		LosSyncDto losSyncDto = new LosSyncDto();
		N1qlQueryRow row = couchBaseIntegrationImpl
				.getSingleField(Prefix.APPLICATION.getCode() + losSyncDto.getApplicationId(), "source");
		JsonObject jsonObject = row.value();
		String source = jsonObject.getString("source");
		losSyncDto.setApplicationId(smeFsaReport.getLoanApplicationId());
		clixWebNotificationServiceImpl.sendWebNotification(losSyncDto, source, null,
				WebNotificationDescription.FSA_FAIL);
	}

	@Override
	public void postProcess(BaseEntity request, BaseResponse response) {

		SmeFsaReport smeFsaReport = (SmeFsaReport) request;
		LOG.info("Inside Post Process for FSA Callback with ApplicationId:{}", request.getId());
		Map<AuditDataKey, String> auditData = new HashMap<>();
		auditData.put(AuditDataKey.APPLICATION_ID, smeFsaReport.getLoanApplicationId());
		auditData.put(AuditDataKey.STAGE, "FSA Callback Post Process");
		auditData.put(AuditDataKey.TX_UUID, request.getUuid());
		auditData.put(AuditDataKey.ERROR, response.getResponse());
		couchBaseIntegrationImpl.saveAuditTrailEntity(auditData);
		LOG.info("Inside FsaCallbackEgestorImpl class postProcess(). AuditTrailEntity Saved.");
	}
}

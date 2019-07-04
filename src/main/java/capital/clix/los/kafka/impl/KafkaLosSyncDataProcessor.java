package capital.clix.los.kafka.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import capital.clix.los.bean.LosSyncDto;
import capital.clix.los.couchbaseIntegration.ICouchBaseIntegration;
import capital.clix.los.enums.ApplicationStage;
import capital.clix.los.enums.LosStatus;
import capital.clix.los.factory.StageUpdateInstance;
import capital.clix.los.kafka.IKafkaLosSyncDataProcessor;
import capital.clix.los.stageupdate.IStageUpdate;
import capital.clix.los.stageupdate.impl.BankStatementStageUpdate;
import capital.clix.los.stageupdate.impl.BureauStageUpdate;
import capital.clix.los.stageupdate.impl.FSAStageUpdate;
import capital.clix.los.stageupdate.impl.GSTStatementUpdate;
import capital.clix.los.stageupdate.impl.PBCommercialBureauStageUpdate;
import capital.clix.los.webService.impl.PushNotificationFactory;

@Service("kafkaLosSyncDataProcessor")
public class KafkaLosSyncDataProcessor implements IKafkaLosSyncDataProcessor {

  @Autowired
  @Qualifier("couchBaseIntegrationImpl")
  private ICouchBaseIntegration couchBaseIntegrationImpl;

  @Autowired
  @Qualifier("pushNotificationFactory")
  PushNotificationFactory pushNotificationFactory;

  @Autowired
  private StageUpdateInstance stageUpdateInstance;

  private static final Logger LOG = LogManager.getLogger(KafkaLosSyncDataProcessor.class);

  @Override
  public void losSyncDataProcessor(LosSyncDto losSyncDto) {

	  LOG.info("Inside Kafka Processor in los");
	  System.out.println();
    if (losSyncDto.getStage().equalsIgnoreCase(ApplicationStage.CREATED.getValue())) {

      IStageUpdate stageInstance = stageUpdateInstance.getInstance(BureauStageUpdate.class);

      // if (losSyncDto.getStatus().equalsIgnoreCase(LosStatus.CIBIL_DONE.name())) {

      stageInstance.updateApplicationStage(losSyncDto, losSyncDto.getApplicationId(),
          LosStatus.DELPHI_DONE);
      // } else if (losSyncDto.getStatus().equalsIgnoreCase(LosStatus.DELPHI_DONE.name())) {

      // stageInstance.updateApplicationStage(losSyncDto, losSyncDto.getApplicationId(),
      // LosStatus.DELPHI_DONE);
      // }
      // else if (losSyncDto.getStatus()
      // .equalsIgnoreCase(LosStatus.COMMERCIAL_REQUEST_SENT.name())) {
      // IStageUpdate stageInstance =
      // stageUpdateInstance.getInstance(PBCommercialBureauStageUpdate.class);
      // stageInstance.updateApplicationStage(losSyncDto, losSyncDto.getApplicationId(),
      // LosStatus.COMMERCIAL_REQUEST_SENT);
      // }

    } else if (losSyncDto.getStage()
        .equalsIgnoreCase(ApplicationStage.STATEMENT_ANALYSIS_INITIATE.getValue())) {

      // if (losSyncDto.getStatus().equalsIgnoreCase(LosStatus.STATEMENT_ANALYSIS_INITIATED.name()))
      // {
      LOG.info(" Processing Bank analysis initiation stage data via BankStatementStageUpdate :{}",
          losSyncDto.toString());
      IStageUpdate stageInstance = stageUpdateInstance.getInstance(BankStatementStageUpdate.class);
      stageInstance.updateApplicationStage(losSyncDto, losSyncDto.getApplicationId(),
          LosStatus.STATEMENT_ANALYSIS_INITIATED);
      // }
    } else if (losSyncDto.getStage()
        .equalsIgnoreCase(ApplicationStage.STATEMENT_ANALYSIS_CALLBACK.getValue())) {

      IStageUpdate stageInstance = stageUpdateInstance.getInstance(BankStatementStageUpdate.class);

      if (losSyncDto.getStatus().equalsIgnoreCase(LosStatus.STATEMENT_ANALYSIS_COMPLETE.name())) {

        if (losSyncDto.getContent() != null && !losSyncDto.getContent().isEmpty()) {


          stageInstance.updateApplicationStage(losSyncDto, losSyncDto.getApplicationId(),
              LosStatus.STATEMENT_ANALYSIS_COMPLETE);
        }
      } else if (losSyncDto.getStatus()
          .equalsIgnoreCase(LosStatus.SME_PERFIOS_DELPHI_COMPLETE.name())) {

        if (losSyncDto.getContent() != null && !losSyncDto.getContent().isEmpty()) {

          stageInstance.updateApplicationStage(losSyncDto, losSyncDto.getApplicationId(),
              LosStatus.SME_PERFIOS_DELPHI_COMPLETE);
        }

      }
    } else if (losSyncDto.getStage().equalsIgnoreCase(ApplicationStage.PB_CREATED.getValue())) {

      IStageUpdate stageInstance =
          stageUpdateInstance.getInstance(PBCommercialBureauStageUpdate.class);
      stageInstance.updateApplicationStage(losSyncDto, losSyncDto.getApplicationId(),
          LosStatus.COMMERCIAL_REQUEST_SENT);

    } else if (losSyncDto.getStage()
        .equalsIgnoreCase(ApplicationStage.PB_FETCH_COMMERCIAL_DELPHI_REPORT_COMPLETE.name())) {

      IStageUpdate stageInstance =
          stageUpdateInstance.getInstance(PBCommercialBureauStageUpdate.class);
      stageInstance.updateApplicationStage(losSyncDto, losSyncDto.getApplicationId(),
          LosStatus.PB_DELPHI_COMMERCIAL_COMPLETE);

    }
    else if (losSyncDto.getStage()
            .equalsIgnoreCase(ApplicationStage.STATEMENT_ANALYSIS_COMPLETE.getValue())||losSyncDto.getStage()
            .equalsIgnoreCase(ApplicationStage.BANK_ANALYSIS_FAILED.getValue())) {
    	System.out.println("Inside Else if condition in kafka of GST");
        IStageUpdate stageInstance =
            stageUpdateInstance.getInstance(GSTStatementUpdate.class);
        stageInstance.updateApplicationStage(losSyncDto, losSyncDto.getApplicationId(),
            LosStatus.GST_COMPLETE);

      }
	
    else if (losSyncDto.getStage()
            .equalsIgnoreCase(ApplicationStage.GST_ANALYSIS_COMPLETED.getValue())||losSyncDto.getStage()
            .equalsIgnoreCase(ApplicationStage.GST_ANALYSIS_FAILED.getValue())) {
    	System.out.println("Inside Else if condition in kafka of FSA");
        IStageUpdate stageInstance =
            stageUpdateInstance.getInstance(FSAStageUpdate.class);
        stageInstance.updateApplicationStage(losSyncDto, losSyncDto.getApplicationId(),
            LosStatus.FSA_ANALYSIS_INITIATED);

      }
    
  }



}

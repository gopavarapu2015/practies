package capital.clix.los.factory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import capital.clix.los.stageupdate.IStageUpdate;

@Component
public class StageUpdateInstance {

  @Autowired
  @Qualifier("bureauStageUpdate")
  IStageUpdate bureauStageUpdate;

  @Autowired
  @Qualifier("bankStatementStageUpdate")
  IStageUpdate bankStatementStageUpdate;

  @Autowired
  @Qualifier("pbCommercialBureauStageUpdate")
  IStageUpdate pbCommercialBureauStageUpdate;
  
  @Autowired
  @Qualifier("gstStatementUpdate")
  IStageUpdate gstStatementUpdate;
  
  @Autowired
  @Qualifier("fsaStageUpdate")
  IStageUpdate fsaStageUpdate;

  private static final Logger LOG = LogManager.getLogger(StageUpdateInstance.class);

  public IStageUpdate getInstance(Class claz) {

    LOG.info("Fetching Instance for claz:- " + claz);
    System.out.println(claz.getName());
    if (claz.getSimpleName().equalsIgnoreCase("BureauStageUpdate")) {
      LOG.info("Retruning Instance for claz:- " + claz);
      return bureauStageUpdate;
    }
    if (claz.getSimpleName().equalsIgnoreCase("BankStatementStageUpdate")) {
      LOG.info("Retruning Instance for claz:- " + claz);
      return bankStatementStageUpdate;
    }
    if (claz.getSimpleName().equalsIgnoreCase("PBCommercialBureauStageUpdate")) {
      LOG.info("Retruning Instance for claz:- " + claz);
      return pbCommercialBureauStageUpdate;
    }
    if(claz.getSimpleName().equalsIgnoreCase("GSTStatementUpdate")) {
    	LOG.info("Retruning Instance for claz:- " + claz);
    	return gstStatementUpdate;
    }
    if(claz.getSimpleName().equalsIgnoreCase("FSAStageUpdate")) {
    	LOG.info("Retruning Instance for claz:- " + claz);
    	return fsaStageUpdate;
    }

    return null;
  }

}

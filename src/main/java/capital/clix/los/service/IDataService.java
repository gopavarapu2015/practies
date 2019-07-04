package capital.clix.los.service;

import java.util.List;
import java.util.Map;
import capital.clix.los.bean.ApplicationClose;
import capital.clix.los.bean.BaseResponse;
import capital.clix.los.bean.bl.ApplicationSearch;
import capital.clix.los.bean.bl.SmePerfiosReport;
import capital.clix.los.bean.cibil.ReportFetchRequest;
import capital.clix.los.enums.ApplicationStage;

public interface IDataService {

  public List<Map> applicationSearch(ApplicationSearch applicationSearch);

  public ApplicationStage fetchStatusById(String id);

  public BaseResponse closeLoan(ApplicationClose applicationClose, BaseResponse response);

  public List getReports(ReportFetchRequest reportFetchRequest);

  public SmePerfiosReport fetchSmePerfiosReport(String id);
}

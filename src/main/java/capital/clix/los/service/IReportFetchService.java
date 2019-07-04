package capital.clix.los.service;

import java.util.List;
import capital.clix.los.bean.cibil.ReportFetchRequest;
import capital.clix.los.bean.cibil.SmeReportFetchResponse;

public interface IReportFetchService {

  public List<SmeReportFetchResponse> fetchReports(ReportFetchRequest reportFetchRequest);
}

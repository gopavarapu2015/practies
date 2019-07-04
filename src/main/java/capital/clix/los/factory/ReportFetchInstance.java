package capital.clix.los.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import capital.clix.los.enums.LoanType;
import capital.clix.los.service.IReportFetchService;

@Component
public class ReportFetchInstance {

  @Autowired
  @Qualifier("smeReportFetchServiceImpl")
  private IReportFetchService smeReportFetchServiceImpl;


  public IReportFetchService getReportFetchServiceInstanceBasedOnType(String type) {

    if (LoanType.BL.getCode().equalsIgnoreCase(type)) {
      return smeReportFetchServiceImpl;
    }
    if (LoanType.LAEP.getCode().equalsIgnoreCase(type)) {
        return smeReportFetchServiceImpl;
      }
    return null;
  }

}

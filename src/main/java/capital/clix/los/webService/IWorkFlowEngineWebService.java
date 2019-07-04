package capital.clix.los.webService;

import capital.clix.los.bean.BaseEntity;
import capital.clix.los.bean.BaseResponse;
import capital.clix.los.bean.WorkFlowRequestBean;

public interface IWorkFlowEngineWebService {

  public void initiateWorkflowEngine(BaseEntity request, BaseResponse response);

  public void MultiBureauCheck(BaseEntity request, BaseResponse response);

  public void SmeStatementAnalysisInitiate(BaseEntity request, BaseResponse response);
  
  public void FinancialStatementAnalysisInitiate(BaseEntity request, BaseResponse response);

  public void perfiosExcelReportFetch(BaseEntity request, BaseResponse response);

  public void fsaExcelReportFetch(BaseEntity request, BaseResponse response);
  public void GstStatementAnalysisInitiate(BaseEntity request, BaseResponse response);

}

package capital.clix.los.egestor;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import capital.clix.los.bean.BaseEntity;
import capital.clix.los.bean.BaseResponse;

@Component
public class EgestionChainer {

  private final IEgestor loanSaveEgestorImpl;

  private final IEgestor idenCheckLoanEgestorImpl;

  private final IEgestor pbLoanBureauInitiateEgestorImpl;

  private final IEgestor loanEntityFetchEgestor;

  private final IEgestor perfiosCallbackEgestorImpl;

  private final IEgestor perfiosExcelReportFetchEgestorImpl;
  private final IEgestor fsaCallbackEgestorImpl;

  private final IEgestor smeBankStatementUpdateEgestorImpl;

  private final IEgestor smeStatementAnalysisInitiateEgestorImpl;

  private final IEgestor UpdateEntityEgestorImpl;

  private final IEgestor pBUpdateCoAppBankWorkflowEgestorImpl;

  private final IEgestor financialStatementAnalysisUpdateEgestorImpl;

  private final IEgestor financialStatementAnalysisInitiateEgestorImpl;
  private final IEgestor gstStatementAnalysisInitiateEgestorImpl;

  private final IEgestor gstStatementAnalysisUpdateEgestorImpl;

  private static final List<IEgestor> createLoanPropagationSequence = new ArrayList<>(2);

  private static final List<IEgestor> updateLoanPropagationSequence = new ArrayList<>(2);

  private static final List<IEgestor> createPBLoanPropagationSequence = new ArrayList<>(2);

  private static final List<IEgestor> fetchLoanPropagationSequence = new ArrayList<>(2);

  private static final List<IEgestor> perfiosReportUpdatePropagationSequence = new ArrayList<>(2);
  private static final List<IEgestor> fsaReportUpdatePropagationSequence = new ArrayList<>(2);
  private static final List<IEgestor> perfiosBankStatementUpdatePropagationSequence =
      new ArrayList<>(2);
  private static final List<IEgestor> financialStatementAnalysisUpdatePropagationSequence =
      new ArrayList<>(2);
  private static final List<IEgestor> gstServiceStatementPropagationSequence = new ArrayList<>(2);

  @Autowired
  public EgestionChainer(@Qualifier("loanSaveEgestorImpl") IEgestor loanSaveEgestorImpl,
      @Qualifier("idenCheckLoanEgestorImpl") IEgestor idenCheckLoanEgestorImpl,
      @Qualifier("pbLoanBureauInitiateEgestorImpl") IEgestor pbLoanBureauInitiateEgestorImpl,
      @Qualifier("loanEntityFetchEgestor") IEgestor loanEntityFetchEgestor,
      @Qualifier("perfiosCallbackEgestorImpl") IEgestor perfiosCallbackEgestorImpl,
      @Qualifier("perfiosExcelReportFetchEgestorImpl") IEgestor perfiosExcelReportFetchEgestorImpl,
      @Qualifier("fsaCallbackEgestorImpl") IEgestor fsaCallbackEgestorImpl,
      @Qualifier("smeBankStatementUpdateEgestorImpl") IEgestor smeBankStatementUpdateEgestorImpl,
      @Qualifier("smeStatementAnalysisInitiateEgestorImpl") IEgestor smeStatementAnalysisInitiateEgestorImpl,
      @Qualifier("updateEntityEgestorImpl") IEgestor updateEntityEgestorImpl,
      @Qualifier("pBUpdateCoAppBankWorkflowEgestorImpl") IEgestor pBUpdateCoAppBankWorkflowEgestorImpl,
      @Qualifier("financialStatementAnalysisUpdateEgestorImpl") IEgestor financialStatementAnalysisUpdateEgestorImpl,
      @Qualifier("financialStatementAnalysisInitiateEgestorImpl") IEgestor financialStatementAnalysisInitiateEgestorImpl,
      @Qualifier("gstStatementAnalysisUpdateEgestorImpl") IEgestor gstStatementAnalysisUpdateEgestorImpl,
      @Qualifier("gstStatementAnalysisInitiateEgestorImpl") IEgestor gstStatementAnalysisInitiateEgestorImpl) {
    this.loanSaveEgestorImpl = loanSaveEgestorImpl;
    this.idenCheckLoanEgestorImpl = idenCheckLoanEgestorImpl;
    this.loanEntityFetchEgestor = loanEntityFetchEgestor;
    this.perfiosCallbackEgestorImpl = perfiosCallbackEgestorImpl;
    this.perfiosExcelReportFetchEgestorImpl = perfiosExcelReportFetchEgestorImpl;
    this.fsaCallbackEgestorImpl = fsaCallbackEgestorImpl;
    this.smeBankStatementUpdateEgestorImpl = smeBankStatementUpdateEgestorImpl;
    this.smeStatementAnalysisInitiateEgestorImpl = smeStatementAnalysisInitiateEgestorImpl;
    this.pbLoanBureauInitiateEgestorImpl = pbLoanBureauInitiateEgestorImpl;
    this.UpdateEntityEgestorImpl = updateEntityEgestorImpl;
    this.pBUpdateCoAppBankWorkflowEgestorImpl = pBUpdateCoAppBankWorkflowEgestorImpl;

    this.financialStatementAnalysisUpdateEgestorImpl = financialStatementAnalysisUpdateEgestorImpl;
    this.financialStatementAnalysisInitiateEgestorImpl =
        financialStatementAnalysisInitiateEgestorImpl;
    this.gstStatementAnalysisUpdateEgestorImpl = gstStatementAnalysisUpdateEgestorImpl;
    this.gstStatementAnalysisInitiateEgestorImpl = gstStatementAnalysisInitiateEgestorImpl;
    createLoanPropagationSequence.add(loanSaveEgestorImpl);
    createLoanPropagationSequence.add(idenCheckLoanEgestorImpl);

    createPBLoanPropagationSequence.add(loanSaveEgestorImpl);
    createPBLoanPropagationSequence.add(pbLoanBureauInitiateEgestorImpl);

    fetchLoanPropagationSequence.add(loanEntityFetchEgestor);

    perfiosReportUpdatePropagationSequence.add(perfiosCallbackEgestorImpl);
    perfiosReportUpdatePropagationSequence.add(perfiosExcelReportFetchEgestorImpl);
    fsaReportUpdatePropagationSequence.add(fsaCallbackEgestorImpl);

    perfiosBankStatementUpdatePropagationSequence.add(smeBankStatementUpdateEgestorImpl);
    perfiosBankStatementUpdatePropagationSequence.add(smeStatementAnalysisInitiateEgestorImpl);

    updateLoanPropagationSequence.add(updateEntityEgestorImpl);
    updateLoanPropagationSequence.add(pBUpdateCoAppBankWorkflowEgestorImpl);


    financialStatementAnalysisUpdatePropagationSequence
        .add(financialStatementAnalysisUpdateEgestorImpl);
    financialStatementAnalysisUpdatePropagationSequence
        .add(financialStatementAnalysisInitiateEgestorImpl);

    gstServiceStatementPropagationSequence.add(gstStatementAnalysisUpdateEgestorImpl);
    gstServiceStatementPropagationSequence.add(gstStatementAnalysisInitiateEgestorImpl);

  }

  public void fetchLoan(BaseEntity request, BaseResponse response) {
    fetchLoanPropagationSequence.forEach(iEgestor -> iEgestor.egest(request, response));

  }

  public void createLoan(BaseEntity request, BaseResponse response) {
    createLoanPropagationSequence.forEach(iEgestor -> iEgestor.egest(request, response));

  }

  public void createPBLoan(BaseEntity request, BaseResponse response) {
    createPBLoanPropagationSequence.forEach(iEgestor -> iEgestor.egest(request, response));

  }

  public void updateLoan(BaseEntity request, BaseResponse response) {
    updateLoanPropagationSequence.forEach(iEgestor -> iEgestor.egest(request, response));

  }

  public void updatePerfiosReport(BaseEntity request, BaseResponse response) {
    perfiosReportUpdatePropagationSequence.forEach(iEgestor -> iEgestor.egest(request, response));

  }

  public void updateFsaReport(BaseEntity request, BaseResponse response) {
    fsaReportUpdatePropagationSequence.forEach(iEgestor -> iEgestor.egest(request, response));
  }

  public void updateBankStatement(BaseEntity request, BaseResponse response) {
    perfiosBankStatementUpdatePropagationSequence
        .forEach(iEgestor -> iEgestor.egest(request, response));

  }

  public void updateFinancialStatementAnalysis(BaseEntity request, BaseResponse response) {
    financialStatementAnalysisUpdatePropagationSequence
        .forEach(iEgestor -> iEgestor.egest(request, response));
  }

  public void updateGstStatement(BaseEntity request, BaseResponse response) {
    gstServiceStatementPropagationSequence.forEach(iEgestor -> iEgestor.egest(request, response));

  }

}

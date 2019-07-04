package capital.clix.los.enums;

public enum ApplicationStage {

  CREATED("CREATED"),
  LAEP_CREATED("LAEP_CREATED"),
  CIBIL_DONE("CIBIL_DONE"),
  STATEMENT_ANALYSIS_CALLBACK("StatementAnalysisCallback"),
  STATEMENT_ANALYSIS_INITIATE("StatementAnalysisInitiate"),
  LAEP_STATEMENT_ANALYSIS_INITIATED("LAEP_STATEMENT_ANALYSIS_INITIATED"),
  CIBIL_FAILED("CibilFailed"),
  STATEMENT_ANALYSIS_COMPLETE("StatementAnalysisComplete"),
  PERFIOS_FAILED("StatementAnalysisFailed"),
  CLOSED("Closed"),
  DELPHI_CIBIL_COMPLETE("Delphi Cibil Completed"),
  CIBIL_COMPLETE_COMMERCIAL_REQUEST_SENT("Cibil Complete Commercial Request Sent"),
  PB_CREATED("PB_CREATED"),
  PB_FETCH_COMMERCIAL_DELPHI_REPORT_COMPLETE("PB Delphi And Commercial Report Fetched Completed"),
  STATEMENT_INITIATION_FAILED("StatementAnalysisInitiate Failed"),
  BANK_ANALYSIS_FAILED("StatementAnalysisFailed"),
  PB_FAILED("CibilFailed"),
  PB_APPROVED("Approved"),
  PB_REJECTED("Rejected"),
  GST_ANALYSIS_INITIATE("GST_ANALYSIS_INITIATE"),GST_ANALYSIS_FAILED(
    "GST_ANALYSIS_FAILED"),GST_ANALYSIS_COMPLETED(
            "GST_ANALYSIS_COMPLETED"),FSA_INITIATION_FAILED("FSA_INITIATION_FAILED"),
  FSA_ANALYSIS_INITIATE("FSA_ANALYSIS_INITIATE"),FSA_ANALYSIS_FAILED(
    		    "FSA_ANALYSIS_FAILED"),FSA_ANALYSIS_COMPLETED(
          "FSA_ANALYSIS_COMPLETED");

  String value;

  ApplicationStage(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }


}

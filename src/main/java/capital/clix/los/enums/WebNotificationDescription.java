package capital.clix.los.enums;

public enum WebNotificationDescription {

  SME_CIBIL_FETCH_SUCCESSFULL("SME_Cibil_Fetch_Successful"), SME_CIBIL_FETCH_FAIL(
      "SME_Cibil_Fetch_Failed"), SME_BANK_ANALYSIS_INITIATE_SUCCESSFULL(
          "SME_Bank_Analysis_Initiate_Successful"), SME_BANK_ANALYSIS_INITIATE_FAIL(
              "SME_Bank_Analysis_Initiate_Failed"), SME_BANK_ANALYSIS_SUCCESSFULL(
                  "SME_Bank_Analysis_Successful"), SME_BANK_ANALYSIS_FAIL(
                      "SME_Bank_Analysis_Failed"), SME_REJECTED("SME_Rejected"), CLIX("CLIX"),GST_ANALYSIS_SUCCESSFUL(
                         "GST_Sucessful"),GST_ANALYSIS_FAIL("GST_Analysis_Fail"),
  FSA_FAIL("Financial_Analysis_Fail"),FSA_ANALYSIS_INITIATE("FSA_Analysis_Initiated"), FSA_ANALYSIS_COMPLETED("FSA_Completed");


  String value;

  WebNotificationDescription(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

}

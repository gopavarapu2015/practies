package capital.clix.los.enums;

public enum Prefix {

  APPLICATION("Application:"), CIBIL_REPORT("Cibil-Report:"), COAPPLICANT(
      "-CoApplicant-"), DELPHI_REPORT("Delphi-Report:"), PERFIOS("Perfios:"),GSTSERVICE_REPORT("Gst-Report:"), GSTSUMMARY("-GstSummary-"),FSA("Fsa:"),GST("Gst:"), PB("PB-");

  String code;

  private Prefix(String code) {
    this.code = code;
  }

  public String getCode() {
    return code;
  }
}

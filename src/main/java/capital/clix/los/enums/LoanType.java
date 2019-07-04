package capital.clix.los.enums;

public enum LoanType {
  PL("PL"), BL("BL"), SME_PB("SME_PB"),LAEP("LAEP");

  String code;

  private LoanType(String code) {
    this.code = code;
  }

  public String getCode() {
    return code;
  }


}

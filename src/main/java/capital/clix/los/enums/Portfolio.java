package capital.clix.los.enums;

public enum Portfolio {
  PL("PL"), SME_BL("SME_BL"), BL("BL");

  private String code;

  Portfolio(String code) {
    this.code = code;
  }

  public String getCode() {
    return code;
  }


}

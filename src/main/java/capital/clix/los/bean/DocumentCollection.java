package capital.clix.los.bean;

public class DocumentCollection extends BaseEntity {

  private String loanApplicationId;
  private String loanType;

  public String getLoanApplicationId() {
    return loanApplicationId;
  }

  public void setLoanApplicationId(String loanApplicationId) {
    this.loanApplicationId = loanApplicationId;
  }

  public String getLoanType() {
    return loanType;
  }

  public void setLoanType(String loanType) {
    this.loanType = loanType;
  }

  @Override
  public String toString() {
    return "DocumentCollection [loanApplicationId=" + loanApplicationId + ", loanType=" + loanType
        + "]";
  }



}

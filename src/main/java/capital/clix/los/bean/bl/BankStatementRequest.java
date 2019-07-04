package capital.clix.los.bean.bl;

import capital.clix.los.bean.BaseEntity;


public class BankStatementRequest extends BaseEntity {

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
    return "BankStatementRequest [loanApplicationId=" + loanApplicationId + ", loanType=" + loanType
        + ", id=" + id + ", toString()=" + super.toString() + "]";
  }


}

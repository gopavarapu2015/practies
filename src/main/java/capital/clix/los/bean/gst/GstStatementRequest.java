package capital.clix.los.bean.gst;

import capital.clix.los.bean.BaseEntity;

public class GstStatementRequest extends BaseEntity{
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

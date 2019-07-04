package capital.clix.los.bean.fsa;

import capital.clix.los.bean.BaseEntity;

public class FinancialStatementAnalysisRequest extends BaseEntity{
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
		return "FinancialStatementAnalysisRequest [loanApplicationId=" + loanApplicationId + ", loanType=" + loanType
				+ "]";
	}

}

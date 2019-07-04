package capital.clix.los.bean;

import com.google.gson.internal.LinkedTreeMap;
import capital.clix.los.enums.LoanType;

public class WorkFlowRequestBean extends BaseEntity {

  private String loanApplicationId;

  private LoanType loanType;

  private String stage;

  private LinkedTreeMap transientData;

  public String getLoanApplicationId() {
    return loanApplicationId;
  }

  public void setLoanApplicationId(String loanApplicationId) {
    this.loanApplicationId = loanApplicationId;
  }

  public LoanType getLoanType() {
    return loanType;
  }

  public void setLoanType(LoanType loanType) {
    this.loanType = loanType;
  }

  public String getStage() {
    return stage;
  }

  public void setStage(String stage) {
    this.stage = stage;
  }


  public LinkedTreeMap getTransientData() {
    return transientData;
  }

  public void setTransientData(LinkedTreeMap transientData) {
    this.transientData = transientData;
  }

  @Override
  public String toString() {
    return "WorkFlowRequestBean [loanApplicationId=" + loanApplicationId + ", loanType=" + loanType
        + ", stage=" + stage + ", id=" + id + ", toString()=" + super.toString() + "]";
  }



}

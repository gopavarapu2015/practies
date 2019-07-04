package capital.clix.los.bean;

public class ProcessorDto {

  private String loanApplicationId;
  private String stage;
  private String workflowCode;
  private String subflowCode;

  public String getLoanApplicationId() {
    return loanApplicationId;
  }

  public void setLoanApplicationId(String loanApplicationId) {
    this.loanApplicationId = loanApplicationId;
  }

  public String getStage() {
    return stage;
  }

  public void setStage(String stage) {
    this.stage = stage;
  }

  public String getWorkflowCode() {
    return workflowCode;
  }

  public void setWorkflowCode(String workflowCode) {
    this.workflowCode = workflowCode;
  }

  public String getSubflowCode() {
    return subflowCode;
  }

  public void setSubflowCode(String subflowCode) {
    this.subflowCode = subflowCode;
  }

  @Override
  public String toString() {
    return "ProcessorDto [loanApplicationId=" + loanApplicationId + ", stage=" + stage
        + ", workflowCode=" + workflowCode + ", subflowCode=" + subflowCode + "]";
  }


}

package capital.clix.los.bean;

import capital.clix.los.enums.ApplicationCloseReason;

public class ApplicationClose extends BaseEntity {

  private String loanApplicationId;
  private String comments;
  private ApplicationCloseReason reason;
  private String closedBy;

  public String getLoanApplicationId() {
    return loanApplicationId;
  }

  public void setLoanApplicationId(String loanApplicationId) {
    this.loanApplicationId = loanApplicationId;
  }

  public String getComments() {
    return comments;
  }

  public void setComments(String comments) {
    this.comments = comments;
  }

  public String getClosedBy() {
    return closedBy;
  }

  public void setClosedBy(String closedBy) {
    this.closedBy = closedBy;
  }

  public ApplicationCloseReason getReason() {
    return reason;
  }

  public void setReason(ApplicationCloseReason reason) {
    this.reason = reason;
  }

  @Override
  public String toString() {
    return "ApplicationClose [loanApplicationId=" + loanApplicationId + ", comments=" + comments
        + ", reason=" + reason + ", closedBy=" + closedBy + "]";
  }



}

package capital.clix.los.bean;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import capital.clix.los.bean.bl.BankStatementRequest;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AdditionalDetails extends BaseEntity {

  private BankStatementRequest bankStatementRequest;
  private List<CoApplicant> coApplicants;

  public BankStatementRequest getBankStatementRequest() {
    return bankStatementRequest;
  }

  public void setBankStatementRequest(BankStatementRequest bankStatementRequest) {
    this.bankStatementRequest = bankStatementRequest;
  }

  public List<CoApplicant> getCoApplicants() {
    return coApplicants;
  }

  public void setCoApplicants(List<CoApplicant> coApplicants) {
    this.coApplicants = coApplicants;
  }

  @Override
  public String toString() {
    return "AdditionalDetails [bankStatementRequest=" + bankStatementRequest + ", coApplicants="
        + coApplicants + "]";
  }



}

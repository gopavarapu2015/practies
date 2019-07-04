package capital.clix.los.bean;

import capital.clix.los.bean.bl.BlLoanEntity;
import capital.clix.los.bean.laep.LAEPLoanEntity;
import capital.clix.los.bean.pb.PBLoanEntity;
import capital.clix.los.enums.LoanType;

public class CommonRequest {

  LoanType loanType;
  BlLoanEntity req;
  PersonalLoanEntity personalLoanEntity;
  PBLoanEntity pbLoanEntity;
  LAEPLoanEntity laepLoanEntity;
  


public LAEPLoanEntity getLaepLoanEntity() {
	return laepLoanEntity;
}

public void setLaepLoanEntity(LAEPLoanEntity laepLoanEntity) {
	this.laepLoanEntity = laepLoanEntity;
}

public LoanType getLoanType() {
    return loanType;
  }

  public void setLoanType(LoanType loanType) {
    this.loanType = loanType;
  }

  public BlLoanEntity getReq() {
    return req;
  }

  public void setReq(BlLoanEntity req) {
    this.req = req;
  }

  public PersonalLoanEntity getPersonalLoanEntity() {
    return personalLoanEntity;
  }

  public void setPersonalLoanEntity(PersonalLoanEntity personalLoanEntity) {
    this.personalLoanEntity = personalLoanEntity;
  }


  public PBLoanEntity getPbLoanEntity() {
    return pbLoanEntity;
  }

  public void setPbLoanEntity(PBLoanEntity pbLoanEntity) {
    this.pbLoanEntity = pbLoanEntity;
  }

  @Override
  public String toString() {
    return "CommonRequest [loanType=" + loanType + ", req=" + req + ", personalLoanEntity="
        + personalLoanEntity + ", toString()=" + super.toString() + "]";
  }


}

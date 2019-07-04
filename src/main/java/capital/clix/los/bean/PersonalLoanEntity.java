package capital.clix.los.bean;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PersonalLoanEntity extends BaseEntity {


  private Applicant applicant;

  private LoanDetails loanDetails;
  private String meetingDay;
  private String meetingTime;

  private List<CoApplicant> coApplicant;
  private String clixAccountNumber;
  private String clixCustNumber;
  private String tnc = "Y";
  private String sender;
  private String clixSourcingBranch;
  private String type;
  private String source;


  public Applicant getApplicant() {
    return applicant;
  }

  public void setApplicant(Applicant applicant) {
    this.applicant = applicant;
  }

  public LoanDetails getLoanDetails() {
    return loanDetails;
  }

  public void setLoanDetails(LoanDetails loanDetails) {
    this.loanDetails = loanDetails;
  }

  public String getMeetingDay() {
    return meetingDay;
  }

  public void setMeetingDay(String meetingDay) {
    this.meetingDay = meetingDay;
  }

  public String getMeetingTime() {
    return meetingTime;
  }

  public void setMeetingTime(String meetingTime) {
    this.meetingTime = meetingTime;
  }

  public List<CoApplicant> getCoApplicant() {
    return coApplicant;
  }

  public void setCoApplicant(List<CoApplicant> coApplicant) {
    this.coApplicant = coApplicant;
  }

  public String getClixAccountNumber() {
    return clixAccountNumber;
  }

  public void setClixAccountNumber(String clixAccountNumber) {
    this.clixAccountNumber = clixAccountNumber;
  }

  public String getClixCustNumber() {
    return clixCustNumber;
  }

  public void setClixCustNumber(String clixCustNumber) {
    this.clixCustNumber = clixCustNumber;
  }

  public String getTnc() {
    return tnc;
  }

  public void setTnc(String tnc) {
    this.tnc = tnc;
  }

  public String getSender() {
    return sender;
  }

  public void setSender(String sender) {
    this.sender = sender;
  }

  public String getClixSourcingBranch() {
    return clixSourcingBranch;
  }

  public void setClixSourcingBranch(String clixSourcingBranch) {
    this.clixSourcingBranch = clixSourcingBranch;
  }

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }


  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }



  @Override
  public String toString() {
    return "PersonalLoanEntity [applicant=" + applicant + ", loanDetails=" + loanDetails
        + ", meetingDay=" + meetingDay + ", meetingTime=" + meetingTime + ", coApplicant="
        + coApplicant + ", clixAccountNumber=" + clixAccountNumber + ", clixCustNumber="
        + clixCustNumber + ", tnc=" + tnc + ", sender=" + sender + ", clixSourcingBranch="
        + clixSourcingBranch + ", source=" + source + "]";
  }


}

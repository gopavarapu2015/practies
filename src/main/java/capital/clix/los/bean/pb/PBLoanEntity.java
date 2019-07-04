package capital.clix.los.bean.pb;

import java.util.List;
import org.springframework.data.couchbase.core.mapping.Document;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import capital.clix.los.bean.Address;
import capital.clix.los.bean.BaseEntity;
import capital.clix.los.bean.CoApplicant;

@JsonIgnoreProperties(ignoreUnknown = true)
@Document
public class PBLoanEntity extends BaseEntity {

  private String loanAmount;
  private String turnOver;
  private String annualProfit;
  private Address currentAddress;// consumer residence address
  private String mobile;
  private String companyType;// ex like partnership propritership
  private String natureOfBusiness;
  private String industryType;// list will be given by vinay
  private String yearsInBusiness;// years in this business
  private String emi;// comfortable emi
  private String fullName;// consumer name
  private String dob;// consumer DOB
  private String email;// consumer email adress
  private String loanInPast;// number of loan in past by consumer
  private String residenceType;// consumer where he is currently staying is rented or owned
  private String companyName;// entity name
  private Address businessAddress;// company address
  private String pan;// company pan
  private Integer unsecuredLoan;// Number of nsecured loans taken in past
  private String gstNumber;// company gst number
  private String officeType;// company rented /owned like
  private String cinNumber;// company cin number
  private String creditType;// fixed value will be "term laons" if they do not give consumer
  private String loanType;//// fixed value will be "term laons" if they do not give company
  private String tenure;
  private List<CoApplicant> coApplicant;
  private String type;
  private String approved;
  private Long createdDateInMilliSec;
  private Long approvedLoanAmount;
  private Integer approvedROI;

  public String getLoanAmount() {
    return loanAmount;
  }

  public void setLoanAmount(String loanAmount) {
    this.loanAmount = loanAmount;
  }

  public String getTurnOver() {
    return turnOver;
  }

  public void setTurnOver(String turnOver) {
    this.turnOver = turnOver;
  }

  public String getAnnualProfit() {
    return annualProfit;
  }

  public void setAnnualProfit(String annualProfit) {
    this.annualProfit = annualProfit;
  }

  public Address getCurrentAddress() {
    return currentAddress;
  }

  public void setCurrentAddress(Address currentAddress) {
    this.currentAddress = currentAddress;
  }

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  public String getCompanyType() {
    return companyType;
  }

  public void setCompanyType(String companyType) {
    this.companyType = companyType;
  }

  public String getNatureOfBusiness() {
    return natureOfBusiness;
  }

  public void setNatureOfBusiness(String natureOfBusiness) {
    this.natureOfBusiness = natureOfBusiness;
  }

  public String getIndustryType() {
    return industryType;
  }

  public void setIndustryType(String industryType) {
    this.industryType = industryType;
  }

  public String getYearsInBusiness() {
    return yearsInBusiness;
  }

  public void setYearsInBusiness(String yearsInBusiness) {
    this.yearsInBusiness = yearsInBusiness;
  }

  public String getEmi() {
    return emi;
  }

  public void setEmi(String emi) {
    this.emi = emi;
  }

  public String getFullName() {
    return fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public String getDob() {
    return dob;
  }

  public void setDob(String dob) {
    this.dob = dob;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getLoanInPast() {
    return loanInPast;
  }

  public void setLoanInPast(String loanInPast) {
    this.loanInPast = loanInPast;
  }

  public String getResidenceType() {
    return residenceType;
  }

  public void setResidenceType(String residenceType) {
    this.residenceType = residenceType;
  }

  public String getCompanyName() {
    return companyName;
  }

  public void setCompanyName(String companyName) {
    this.companyName = companyName;
  }

  public Address getBusinessAddress() {
    return businessAddress;
  }

  public void setBusinessAddress(Address businessAddress) {
    this.businessAddress = businessAddress;
  }

  public String getPan() {
    return pan;
  }

  public void setPan(String pan) {
    this.pan = pan;
  }

  public Integer getUnsecuredLoan() {
    return unsecuredLoan;
  }

  public void setUnsecuredLoan(Integer unsecuredLoan) {
    this.unsecuredLoan = unsecuredLoan;
  }

  public String getGstNumber() {
    return gstNumber;
  }

  public void setGstNumber(String gstNumber) {
    this.gstNumber = gstNumber;
  }

  public String getOfficeType() {
    return officeType;
  }

  public void setOfficeType(String officeType) {
    this.officeType = officeType;
  }

  public String getCinNumber() {
    return cinNumber;
  }

  public void setCinNumber(String cinNumber) {
    this.cinNumber = cinNumber;
  }

  public String getCreditType() {
    return creditType;
  }

  public void setCreditType(String creditType) {
    this.creditType = creditType;
  }

  public String getLoanType() {
    return loanType;
  }

  public void setLoanType(String loanType) {
    this.loanType = loanType;
  }

  public String getTenure() {
    return tenure;
  }

  public void setTenure(String tenure) {
    this.tenure = tenure;
  }

  public List<CoApplicant> getCoApplicant() {
    return coApplicant;
  }

  public void setCoApplicant(List<CoApplicant> coApplicant) {
    this.coApplicant = coApplicant;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }


  public String getApproved() {
    return approved;
  }

  public void setApproved(String approved) {
    this.approved = approved;
  }

  public Long getCreatedDateInMilliSec() {
    return createdDateInMilliSec;
  }

  public void setCreatedDateInMilliSec(Long createdDateInMilliSec) {
    this.createdDateInMilliSec = createdDateInMilliSec;
  }


  public Long getApprovedLoanAmount() {
    return approvedLoanAmount;
  }

  public void setApprovedLoanAmount(Long approvedLoanAmount) {
    this.approvedLoanAmount = approvedLoanAmount;
  }

  public Integer getApprovedROI() {
    return approvedROI;
  }

  public void setApprovedROI(Integer approvedROI) {
    this.approvedROI = approvedROI;
  }

  @Override
  public String toString() {
    return "PBLoanEntity [loanAmount=" + loanAmount + ", turnOver=" + turnOver + ", annualProfit="
        + annualProfit + ", currentAddress=" + currentAddress + ", mobile=" + mobile
        + ", companyType=" + companyType + ", natureOfBusiness=" + natureOfBusiness
        + ", industryType=" + industryType + ", yearsInBusiness=" + yearsInBusiness + ", emi=" + emi
        + ", fullName=" + fullName + ", dob=" + dob + ", email=" + email + ", loanInPast="
        + loanInPast + ", residenceType=" + residenceType + ", companyName=" + companyName
        + ", businessAddress=" + businessAddress + ", pan=" + pan + ", unsecuredLoan="
        + unsecuredLoan + ", gstNumber=" + gstNumber + ", officeType=" + officeType + ", cinNumber="
        + cinNumber + ", creditType=" + creditType + ", loanType=" + loanType + ", tenure=" + tenure
        + ", coApplicant=" + coApplicant + "]";
  }

  // for delphi we will provide businessadress pincode and coapp1-address pincode,office type


}

package capital.clix.los.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Applicant extends BaseApplicant {

  private String category = "INDIVIDUAL";

  private Address tempAddress;

  private Address permAddress;
  private String aadhar;
  private String ckycNumber;

  private String pan;


  private String chooseName;

  private String clientConstitution;

  private String netIncome;

  private String employer;

  private BankDetails bankDetails;

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public Address getTempAddress() {
    return tempAddress;
  }

  public void setTempAddress(Address tempAddress) {
    this.tempAddress = tempAddress;
  }

  public Address getPermAddress() {
    return permAddress;
  }

  public void setPermAddress(Address permAddress) {
    this.permAddress = permAddress;
  }

  public String getAadhar() {
    return aadhar;
  }

  public void setAadhar(String aadhar) {
    this.aadhar = aadhar;
  }

  public String getCkycNumber() {
    return ckycNumber;
  }

  public void setCkycNumber(String ckycNumber) {
    this.ckycNumber = ckycNumber;
  }

  public String getPan() {
    return pan;
  }

  public void setPan(String pan) {
    this.pan = pan;
  }

  public String getChooseName() {
    return chooseName;
  }

  public void setChooseName(String chooseName) {
    this.chooseName = chooseName;
  }

  public String getClientConstitution() {
    return clientConstitution;
  }

  public void setClientConstitution(String clientConstitution) {
    this.clientConstitution = clientConstitution;
  }

  public String getNetIncome() {
    return netIncome;
  }

  public void setNetIncome(String netIncome) {
    this.netIncome = netIncome;
  }

  public String getEmployer() {
    return employer;
  }

  public void setEmployer(String employer) {
    this.employer = employer;
  }

  public BankDetails getBankDetails() {
    return bankDetails;
  }

  public void setBankDetails(BankDetails bankDetails) {
    this.bankDetails = bankDetails;
  }

  @Override
  public String toString() {
    return "Applicant [category=" + category + ", tempAddress=" + tempAddress + ", permAddress="
        + permAddress + ", aadhar=" + aadhar + ", ckycNumber=" + ckycNumber + ", pan=" + pan
        + ", chooseName=" + chooseName + ", clientConstitution=" + clientConstitution
        + ", netIncome=" + netIncome + ", employer=" + employer + ", bankDetails=" + bankDetails
        + "]";
  }


}

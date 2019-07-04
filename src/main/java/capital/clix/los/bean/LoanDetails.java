package capital.clix.los.bean;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = Include.NON_NULL)
public class LoanDetails implements Serializable {

  private String productCode;

  private String appliedAmount;

  private String tenure;

  private String emi;

  private String comfortableEmi;

  private String proposedUsed;

  private String receivedROI;
  private String organisation = "CLIX CAPITAL";

  public String getProductCode() {
    return productCode;
  }

  public void setProductCode(String productCode) {
    this.productCode = productCode;
  }

  public String getAppliedAmount() {
    return appliedAmount;
  }

  public void setAppliedAmount(String appliedAmount) {
    this.appliedAmount = appliedAmount;
  }

  public String getTenure() {
    return tenure;
  }

  public void setTenure(String tenure) {
    this.tenure = tenure;
  }

  public String getEmi() {
    return emi;
  }

  public void setEmi(String emi) {
    this.emi = emi;
  }

  public String getComfortableEmi() {
    return comfortableEmi;
  }

  public void setComfortableEmi(String comfortableEmi) {
    this.comfortableEmi = comfortableEmi;
  }

  public String getProposedUsed() {
    return proposedUsed;
  }

  public void setProposedUsed(String proposedUsed) {
    this.proposedUsed = proposedUsed;
  }

  public String getReceivedROI() {
    return receivedROI;
  }

  public void setReceivedROI(String receivedROI) {
    this.receivedROI = receivedROI;
  }

  public String getOrganisation() {
    return organisation;
  }

  public void setOrganisation(String organisation) {
    this.organisation = organisation;
  }

  @Override
  public String toString() {
    return "LoanDetails [productCode=" + productCode + ", appliedAmount=" + appliedAmount
        + ", tenure=" + tenure + ", emi=" + emi + ", comfortableEmi=" + comfortableEmi
        + ", proposedUsed=" + proposedUsed + ", receivedROI=" + receivedROI + ", organisation="
        + organisation + "]";
  }


}

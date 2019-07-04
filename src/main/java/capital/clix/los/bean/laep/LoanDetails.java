package capital.clix.los.bean.laep;

public class LoanDetails {
	private String productCode;

	  private String appliedAmount;

	  private String tenure;

	  private String emi;

	  private String dailyEMI;

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
	public String getDailyEMI() {
		return dailyEMI;
	}
	public void setDailyEMI(String dailyEMI) {
		this.dailyEMI = dailyEMI;
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

}

package capital.clix.los.bean.laep;


import java.util.ArrayList;

import java.util.List;

import capital.clix.los.bean.Address;
import capital.clix.los.bean.ApplicationClose;
import capital.clix.los.bean.BaseEntity;
import capital.clix.los.bean.CoApplicant;
import capital.clix.los.bean.GSTNSummary;
import capital.clix.los.bean.LoanDetails;


public class LAEPLoanEntity extends BaseEntity{
	 private String portfolio;
	  private String gstn;
	  private Long incorporation;
	  private String registeredName;
	  private String clientConstitution;
	  private String industry;
	  private String segment;
	  private String product;
	  private String businessNature;
	  private Integer turnOver;
	  private String pan;
	  private String TradingName;
	  private String VATNumber;
	  private String sourcingChannel;
	  private String lineOfBusiness;
	  private String NoOfMachines;
	  private String Processor;
	  private String NoOfBranches;
	  private Address regAddress;
	  private Address bussinessAddress;
	  private List<CoApplicant> coApplicant;
	  private LoanDetails loanDetails;
	  private String type;
	  private String ownerCode;
	  private String ownerMobile;
	  private String ownerEmail;
	  private String loanType;
	  private ApplicationClose applicationClose;
	  private String dsaName;
	  private Boolean isOfficeResiSame;
	  private Boolean isOfficeResiDemarcated;
	  private List<GSTNSummary> gstnSummary;
	public String getPortfolio() {
		return portfolio;
	}
	public void setPortfolio(String portfolio) {
		this.portfolio = portfolio;
	}
	public String getGstn() {
		return gstn;
	}
	public void setGstn(String gstn) {
		this.gstn = gstn;
	}
	public Long getIncorporation() {
		return incorporation;
	}
	public void setIncorporation(Long incorporation) {
		this.incorporation = incorporation;
	}
	public String getRegisteredName() {
		return registeredName;
	}
	public void setRegisteredName(String registeredName) {
		this.registeredName = registeredName;
	}
	public String getClientConstitution() {
		return clientConstitution;
	}
	public void setClientConstitution(String clientConstitution) {
		this.clientConstitution = clientConstitution;
	}
	public String getIndustry() {
		return industry;
	}
	public void setIndustry(String industry) {
		this.industry = industry;
	}
	public String getSegment() {
		return segment;
	}
	public void setSegment(String segment) {
		this.segment = segment;
	}
	public String getProduct() {
		return product;
	}
	public void setProduct(String product) {
		this.product = product;
	}
	public String getBusinessNature() {
		return businessNature;
	}
	public void setBusinessNature(String businessNature) {
		this.businessNature = businessNature;
	}
	public Integer getTurnOver() {
		return turnOver;
	}
	public void setTurnOver(Integer turnOver) {
		this.turnOver = turnOver;
	}
	public String getPan() {
		return pan;
	}
	public void setPan(String pan) {
		this.pan = pan;
	}
	public String getTradingName() {
		return TradingName;
	}
	public void setTradingName(String tradingName) {
		TradingName = tradingName;
	}
	public String getVATNumber() {
		return VATNumber;
	}
	public void setVATNumber(String vATNumber) {
		VATNumber = vATNumber;
	}
	public String getSourcingChannel() {
		return sourcingChannel;
	}
	public void setSourcingChannel(String sourcingChannel) {
		this.sourcingChannel = sourcingChannel;
	}
	public String getLineOfBusiness() {
		return lineOfBusiness;
	}
	public void setLineOfBusiness(String lineOfBusiness) {
		this.lineOfBusiness = lineOfBusiness;
	}
	public String getNoOfMachines() {
		return NoOfMachines;
	}
	public void setNoOfMachines(String noOfMachines) {
		NoOfMachines = noOfMachines;
	}
	public String getProcessor() {
		return Processor;
	}
	public void setProcessor(String processor) {
		Processor = processor;
	}
	public String getNoOfBranches() {
		return NoOfBranches;
	}
	public void setNoOfBranches(String noOfBranches) {
		NoOfBranches = noOfBranches;
	}
	public Address getRegAddress() {
		return regAddress;
	}
	public void setRegAddress(Address regAddress) {
		this.regAddress = regAddress;
	}
	public Address getBussinessAddress() {
		return bussinessAddress;
	}
	public void setBussinessAddress(Address bussinessAddress) {
		this.bussinessAddress = bussinessAddress;
	}
	public List<CoApplicant> getCoApplicant() {
		return coApplicant;
	}
	public void setCoApplicant(List<CoApplicant> coApplicant) {
		this.coApplicant = coApplicant;
	}
	public LoanDetails getLoanDetails() {
		return loanDetails;
	}
	public void setLoanDetails(LoanDetails loanDetails) {
		this.loanDetails = loanDetails;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getOwnerCode() {
		return ownerCode;
	}
	public void setOwnerCode(String ownerCode) {
		this.ownerCode = ownerCode;
	}
	public String getOwnerMobile() {
		return ownerMobile;
	}
	public void setOwnerMobile(String ownerMobile) {
		this.ownerMobile = ownerMobile;
	}
	public String getOwnerEmail() {
		return ownerEmail;
	}
	public void setOwnerEmail(String ownerEmail) {
		this.ownerEmail = ownerEmail;
	}
	public String getLoanType() {
		return loanType;
	}
	public void setLoanType(String loanType) {
		this.loanType = loanType;
	}
	public ApplicationClose getApplicationClose() {
		return applicationClose;
	}
	public void setApplicationClose(ApplicationClose applicationClose) {
		this.applicationClose = applicationClose;
	}
	public String getDsaName() {
		return dsaName;
	}
	public void setDsaName(String dsaName) {
		this.dsaName = dsaName;
	}
	public Boolean getIsOfficeResiSame() {
		return isOfficeResiSame;
	}
	public void setIsOfficeResiSame(Boolean isOfficeResiSame) {
		this.isOfficeResiSame = isOfficeResiSame;
	}
	public Boolean getIsOfficeResiDemarcated() {
		return isOfficeResiDemarcated;
	}
	public void setIsOfficeResiDemarcated(Boolean isOfficeResiDemarcated) {
		this.isOfficeResiDemarcated = isOfficeResiDemarcated;
	}
	public List<GSTNSummary> getGstnSummary() {
		return gstnSummary;
	}
	public void setGstnSummary(List<GSTNSummary> gstnSummary) {
		this.gstnSummary = gstnSummary;
	}
	  

}

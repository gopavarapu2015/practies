package capital.clix.los.bean.fsa;

import capital.clix.los.bean.cibil.SmeReportFetchResponse;

public class BalanceSummaryReport extends SmeReportFetchResponse{
	private String year;
	private String turnOver;
	private String operatingProfit;
	private String totalNetWorth;
	private String revenueGrowth;
	private String patMargin;
	private String operatingProfitMargin;
	private String pbditorCapitalEmployed;
	private String debtEquity;
	private String interestCoverageRatio;
	private String debtorPbdit;
	private String currentRatio;
	private String receivableDays;
	private String inventoryDays;
	
	public String getTurnOver() {
		return turnOver;
	}
	public void setTurnOver(String turnOver) {
		this.turnOver = turnOver;
	}
	public String getOperatingProfit() {
		return operatingProfit;
	}
	public void setOperatingProfit(String operatingProfit) {
		this.operatingProfit = operatingProfit;
	}
	public String getTotalNetWorth() {
		return totalNetWorth;
	}
	public void setTotalNetWorth(String totalNetWorth) {
		this.totalNetWorth = totalNetWorth;
	}
	public String getRevenueGrowth() {
		return revenueGrowth;
	}
	public void setRevenueGrowth(String revenueGrowth) {
		this.revenueGrowth = revenueGrowth;
	}
	public String getPatMargin() {
		return patMargin;
	}
	public void setPatMargin(String patMargin) {
		this.patMargin = patMargin;
	}
	public String getOperatingProfitMargin() {
		return operatingProfitMargin;
	}
	public void setOperatingProfitMargin(String operatingProfitMargin) {
		this.operatingProfitMargin = operatingProfitMargin;
	}
	public String getPbditorCapitalEmployed() {
		return pbditorCapitalEmployed;
	}
	public void setPbditorCapitalEmployed(String pbditorCapitalEmployed) {
		this.pbditorCapitalEmployed = pbditorCapitalEmployed;
	}
	public String getDebtEquity() {
		return debtEquity;
	}
	public void setDebtEquity(String debtEquity) {
		this.debtEquity = debtEquity;
	}
	public String getInterestCoverageRatio() {
		return interestCoverageRatio;
	}
	public void setInterestCoverageRatio(String interestCoverageRatio) {
		this.interestCoverageRatio = interestCoverageRatio;
	}
	public String getDebtorPbdit() {
		return debtorPbdit;
	}
	public void setDebtorPbdit(String debtorPbdit) {
		this.debtorPbdit = debtorPbdit;
	}
	public String getCurrentRatio() {
		return currentRatio;
	}
	public void setCurrentRatio(String currentRatio) {
		this.currentRatio = currentRatio;
	}
	public String getReceivableDays() {
		return receivableDays;
	}
	public void setReceivableDays(String receivableDays) {
		this.receivableDays = receivableDays;
	}
	public String getInventoryDays() {
		return inventoryDays;
	}
	public void setInventoryDays(String inventoryDays) {
		this.inventoryDays = inventoryDays;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	@Override
	public String toString() {
		return "BalanceSummaryReport [year=" + year + ", turnOver=" + turnOver + ", operatingProfit=" + operatingProfit
				+ ", totalNetWorth=" + totalNetWorth + ", revenueGrowth=" + revenueGrowth + ", patMargin=" + patMargin
				+ ", operatingProfitMargin=" + operatingProfitMargin + ", pbditorCapitalEmployed="
				+ pbditorCapitalEmployed + ", debtEquity=" + debtEquity + ", interestCoverageRatio="
				+ interestCoverageRatio + ", debtorPbdit=" + debtorPbdit + ", currentRatio=" + currentRatio
				+ ", receivableDays=" + receivableDays + ", inventoryDays=" + inventoryDays + "]";
	}
	
	
	
	
	

}
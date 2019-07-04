package capital.clix.los.bean.fsa;

import capital.clix.los.bean.cibil.SmeReportFetchResponse;

public class BalanceSummaryFsaReport extends SmeReportFetchResponse{

	private String year;
	
	private String profitOrLossAsperBooks;

	private String profitOrLossExclNonBusinessIncome;

	private String salesOrReceipts;

	private String ebitda;

	private String netWorkingCapital;

	private String debtorDays;

	private String totalDebtOrEBITDA;

	private String interestCoverageRatio;

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getProfitOrLossAsperBooks() {
		return profitOrLossAsperBooks;
	}

	public void setProfitOrLossAsperBooks(String profitOrLossAsperBooks) {
		this.profitOrLossAsperBooks = profitOrLossAsperBooks;
	}

	public String getProfitOrLossExclNonBusinessIncome() {
		return profitOrLossExclNonBusinessIncome;
	}

	public void setProfitOrLossExclNonBusinessIncome(String profitOrLossExclNonBusinessIncome) {
		this.profitOrLossExclNonBusinessIncome = profitOrLossExclNonBusinessIncome;
	}

	public String getSalesOrReceipts() {
		return salesOrReceipts;
	}

	public void setSalesOrReceipts(String salesOrReceipts) {
		this.salesOrReceipts = salesOrReceipts;
	}

	public String getEbitda() {
		return ebitda;
	}

	public void setEbitda(String ebitda) {
		this.ebitda = ebitda;
	}

	public String getNetWorkingCapital() {
		return netWorkingCapital;
	}

	public void setNetWorkingCapital(String netWorkingCapital) {
		this.netWorkingCapital = netWorkingCapital;
	}

	public String getDebtorDays() {
		return debtorDays;
	}

	public void setDebtorDays(String debtorDays) {
		this.debtorDays = debtorDays;
	}

	public String getTotalDebtOrEBITDA() {
		return totalDebtOrEBITDA;
	}

	public void setTotalDebtOrEBITDA(String totalDebtOrEBITDA) {
		this.totalDebtOrEBITDA = totalDebtOrEBITDA;
	}

	public String getInterestCoverageRatio() {
		return interestCoverageRatio;
	}

	public void setInterestCoverageRatio(String interestCoverageRatio) {
		this.interestCoverageRatio = interestCoverageRatio;
	}



	@Override
	public String toString() {
		return "BalanceSummaryFsaReport [year=" + year + ", profitOrLossAsperBooks=" + profitOrLossAsperBooks
				+ ", profitOrLossExclNonBusinessIncome=" + profitOrLossExclNonBusinessIncome + ", salesOrReceipts="
				+ salesOrReceipts + ", ebitda=" + ebitda + ", netWorkingCapital=" + netWorkingCapital + ", debtorDays="
				+ debtorDays + ", totalDebtOrEBITDA=" + totalDebtOrEBITDA + ", interestCoverageRatio="
				+ interestCoverageRatio + "]";
	}

}

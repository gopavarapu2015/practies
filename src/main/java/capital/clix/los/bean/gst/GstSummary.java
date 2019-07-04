package capital.clix.los.bean.gst;

import java.util.ArrayList;
import java.util.List;

import capital.clix.los.bean.bl.BlLoanEntity;
import capital.clix.los.bean.cibil.SmeReportFetchResponse;

public class GstSummary extends SmeReportFetchResponse{
private String gstNumber; 
private String annualizedTurnOver;
private List<MonthwiseSummary> monthwiseSummary = new ArrayList<>();
public String getAnnualizedTurnOver() {
	return annualizedTurnOver;
}
public void setAnnualizedTurnOver(String annualizedTurnOver) {
	this.annualizedTurnOver = annualizedTurnOver;
}
public String getGstNumber() {
	return gstNumber;
}
public void setGstNumber(String gstNumber) {
	this.gstNumber = gstNumber;
}
public List<MonthwiseSummary> getMonthwiseSummary() {
	return monthwiseSummary;
}
public void setMonthwiseSummary(List<MonthwiseSummary> monthwiseSummary) {
	this.monthwiseSummary = monthwiseSummary;
}

}

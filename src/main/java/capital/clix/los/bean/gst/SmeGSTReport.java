package capital.clix.los.bean.gst;

import org.springframework.data.couchbase.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import capital.clix.los.bean.cibil.SmeReportFetchResponse;
import capital.clix.util.JsonMarshallingUtil;

@Document
@JsonIgnoreProperties(ignoreUnknown = true)
public class SmeGSTReport extends SmeReportFetchResponse{

	private String loanApplicationId;
	private String applicantReports;
	private Boolean success;
	private String transactionCode;
	private String applicantCode;

	private static final Gson GSON = new Gson();

	public LinkedTreeMap getApplicantReports() {

		return GSON.fromJson(this.applicantReports, LinkedTreeMap.class);
	}

	public void setApplicantReports(LinkedTreeMap applicantReports) {
		this.applicantReports = JsonMarshallingUtil.toString(applicantReports);
	}

	public String getLoanApplicationId() {
		return loanApplicationId;
	}

	public void setLoanApplicationId(String loanApplicationId) {
		this.loanApplicationId = loanApplicationId;
	}

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public String getTransactionCode() {
		return transactionCode;
	}

	public void setTransactionCode(String transactionCode) {
		this.transactionCode = transactionCode;
	}

	public String getApplicantCode() {
		return applicantCode;
	}

	public void setApplicantCode(String applicantCode) {
		this.applicantCode = applicantCode;
	}

	@Override
	public String toString() {
		return "SmeFsaReport [loanApplicationId=" + loanApplicationId + ", applicantReports=" + applicantReports
				+ ", success=" + success + ", transactionCode=" + transactionCode + ", applicantCode=" + applicantCode
				+ "]";
	}


}

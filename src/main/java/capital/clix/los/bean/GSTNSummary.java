package capital.clix.los.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = Include.NON_NULL)
public class GSTNSummary {
	private String month;
	  private String gstn1;
	  private String gstn2;
	  private String gstn3;
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public String getGstn1() {
		return gstn1;
	}
	public void setGstn1(String gstn1) {
		this.gstn1 = gstn1;
	}
	public String getGstn2() {
		return gstn2;
	}
	public void setGstn2(String gstn2) {
		this.gstn2 = gstn2;
	}
	public String getGstn3() {
		return gstn3;
	}
	public void setGstn3(String gstn3) {
		this.gstn3 = gstn3;
	}
	  
}

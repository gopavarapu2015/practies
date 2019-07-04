package capital.clix.los.bean;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = Include.NON_NULL)
public class Address implements Serializable {

  private String address1;
  private String address2;
  private String locality;
  private String pincode;
  private String country = "INDIA";
  private String state;
  private String city;
  private String presentSince;
  private String rentedOrOwned;
  private Long stayDuration;

  public String getAddress1() {
    return address1;
  }

  public void setAddress1(String address1) {
    this.address1 = address1;
  }

  public String getAddress2() {
    return address2;
  }

  public void setAddress2(String address2) {
    this.address2 = address2;
  }

  public String getAddress3() {
    return locality;
  }

  public void setAddress3(String address3) {
    this.locality = address3;
  }

  public String getPincode() {
    return pincode;
  }

  public void setPincode(String pincode) {
    this.pincode = pincode;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getPresentSince() {
    return presentSince;
  }

  public void setPresentSince(String presentSince) {
    this.presentSince = presentSince;
  }

  public String getRentedOrOwned() {
    return rentedOrOwned;
  }

  public void setRentedOrOwned(String rentedOrOwned) {
    this.rentedOrOwned = rentedOrOwned;
  }

  public Long getStayDuration() {
    return stayDuration;
  }

  public void setStayDuration(Long stayDuration) {
    this.stayDuration = stayDuration;
  }

  @Override
  public String toString() {
    return "Address [address1=" + address1 + ", address2=" + address2 + ", address3=" + locality
        + ", pincode=" + pincode + ", country=" + country + ", state=" + state + ", city=" + city
        + ", presentSince=" + presentSince + ", rentedOrOwned=" + rentedOrOwned + ", stayDuration="
        + stayDuration + ", toString()=" + super.toString() + "]";
  }
}

package capital.clix.los.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = Include.NON_NULL)
public class CoApplicant extends BaseApplicant {

  private String id;
  private String relationship;
  private String percHolding;
  private String pan;
  private String aadhaar;
  private String groupKey;
  private Address currAddress;
  private Address permAddress;
  private String panIdenCheckScore;

  public String getRelationship() {
    return relationship;
  }

  public void setRelationship(String relationship) {
    this.relationship = relationship;
  }

  public String getPercHolding() {
    return percHolding;
  }

  public void setPercHolding(String percHolding) {
    this.percHolding = percHolding;
  }

  public String getPan() {
    return pan;
  }

  public void setPan(String pan) {
    this.pan = pan;
  }

  public String getAadhaar() {
    return aadhaar;
  }

  public void setAadhaar(String aadhaar) {
    this.aadhaar = aadhaar;
  }

  public Address getCurrAddress() {
    return currAddress;
  }

  public void setCurrAddress(Address currAddress) {
    this.currAddress = currAddress;
  }

  public Address getPermAddress() {
    return permAddress;
  }

  public void setPermAddress(Address permAddress) {
    this.permAddress = permAddress;
  }


  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getPanIdenCheckScore() {
    return panIdenCheckScore;
  }

  public void setPanIdenCheckScore(String panIdenCheckScore) {
    this.panIdenCheckScore = panIdenCheckScore;
  }

  public String getGroupKey() {
    return groupKey;
  }

  public void setGroupKey(String groupKey) {
    this.groupKey = groupKey;
  }

  @Override
  public String toString() {
    return "CoApplicant [id=" + id + ", relationship=" + relationship + ", percHolding="
        + percHolding + ", pan=" + pan + ", aadhaar=" + aadhaar + ", currAddress=" + currAddress
        + ", permAddress=" + permAddress + ", panIdenCheckScore=" + panIdenCheckScore
        + ", toString()=" + super.toString() + "]";
  }


}

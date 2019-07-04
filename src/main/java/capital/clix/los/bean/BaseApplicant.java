package capital.clix.los.bean;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = Include.NON_NULL)
public class BaseApplicant implements Serializable {


  private String title;

  private String firstName;

  private String middleName;

  private String lastName;

  private String dob;

  private String age;

  private String gender;

  private String registeredEmail;

  private String registeredPhone;

  private String fatherName;

  private String motherMaidenName;
  private String mobile;
  private String maritalStatus;



  private Map<String, List<String>> documentToDmsMap;

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getMiddleName() {
    return middleName;
  }

  public void setMiddleName(String middleName) {
    this.middleName = middleName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getAge() {
    return age;
  }

  public void setAge(String age) {
    this.age = age;
  }

  public String getGender() {
    return gender;
  }

  public void setGender(String gender) {
    this.gender = gender;
  }

  public String getRegisteredEmail() {
    return registeredEmail;
  }

  public void setRegisteredEmail(String registeredEmail) {
    this.registeredEmail = registeredEmail;
  }

  public String getRegisteredPhone() {
    return registeredPhone;
  }

  public void setRegisteredPhone(String registeredPhone) {
    this.registeredPhone = registeredPhone;
  }

  public String getFatherName() {
    return fatherName;
  }

  public void setFatherName(String fatherName) {
    this.fatherName = fatherName;
  }

  public String getMotherMaidenName() {
    return motherMaidenName;
  }

  public void setMotherMaidenName(String motherMaidenName) {
    this.motherMaidenName = motherMaidenName;
  }

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  public String getMaritalStatus() {
    return maritalStatus;
  }

  public void setMaritalStatus(String maritalStatus) {
    this.maritalStatus = maritalStatus;
  }

  public Map<String, List<String>> getDocumentToDmsMap() {
    return documentToDmsMap;
  }

  public void setDocumentToDmsMap(Map<String, List<String>> documentToDmsMap) {
    this.documentToDmsMap = documentToDmsMap;
  }

  public String getDob() {
    return dob;
  }

  public void setDob(String dob) {
    this.dob = dob;
  }

  @Override
  public String toString() {
    return "BaseApplicant [title=" + title + ", firstName=" + firstName + ", middleName="
        + middleName + ", lastName=" + lastName + ", dob=" + dob + ", age=" + age + ", gender="
        + gender + ", registeredEmail=" + registeredEmail + ", registeredPhone=" + registeredPhone
        + ", fatherName=" + fatherName + ", motherMaidenName=" + motherMaidenName + ", mobile="
        + mobile + ", maritalStatus=" + maritalStatus + ", documentToDmsMap=" + documentToDmsMap
        + ", toString()=" + super.toString() + "]";
  }

}

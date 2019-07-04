package capital.clix.los.bean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LosSyncDto implements Serializable {

  private String applicationId;
  private String stage;
  private String status;
  private String assignedTo;
  private Map<String, String> applicationData = new HashMap<>();
  private Map<String, Object> content = new HashMap<>(2);
  private Map<String, String> notificationDataMap = new HashMap<>();
  private ErrorResponseDto errorResponseDto;

  public String getApplicationId() {
    return applicationId;
  }

  public void setApplicationId(String applicationId) {
    this.applicationId = applicationId;
  }

  public String getStage() {
    return stage;
  }

  public void setStage(String stage) {
    this.stage = stage;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getAssignedTo() {
    return assignedTo;
  }

  public void setAssignedTo(String assignedTo) {
    this.assignedTo = assignedTo;
  }

  public Map<String, String> getApplicationData() {
    return applicationData;
  }

  public void setApplicationData(Map<String, String> applicationData) {
    this.applicationData = applicationData;
  }

  public Map<String, Object> getContent() {
    return content;
  }

  public void setContent(Map<String, Object> content) {
    this.content = content;
  }

  public ErrorResponseDto getErrorResponseDto() {
    return errorResponseDto;
  }

  public void setErrorResponseDto(ErrorResponseDto errorResponseDto) {
    this.errorResponseDto = errorResponseDto;
  }

  @Override
  public String toString() {
    return "LosSyncDto [applicationId=" + applicationId + ", stage=" + stage + ", status=" + status
        + ", assignedTo=" + assignedTo + ", applicationData=" + applicationData + ", content="
        + content + ", notificationDataMap=" + notificationDataMap + ", errorResponseDto="
        + errorResponseDto + ", toString()=" + super.toString() + "]";
  }



}

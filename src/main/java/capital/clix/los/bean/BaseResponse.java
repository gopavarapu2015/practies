package capital.clix.los.bean;

import java.util.ArrayList;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class BaseResponse {

  private String applicationId;
  private String response;
  private Integer responseStatus;
  private boolean isSuccess;
  private ArrayList<String> errorMessage;
  private BaseEntity losApplicationData;
  private ErrorResponseDto errorResponseDto;


  public String getApplicationId() {
    return applicationId;
  }

  public void setApplicationId(String applicationId) {
    this.applicationId = applicationId;
  }

  public String getResponse() {
    return response;
  }

  public void setResponse(String response) {
    this.response = response;
  }

  public boolean isSuccess() {
    return isSuccess;
  }

  public void setSuccess(boolean isSuccess) {
    this.isSuccess = isSuccess;
  }

  public ArrayList<String> getErrorMessage() {
    return errorMessage;
  }

  public void setErrorMessage(ArrayList<String> errorMessage) {
    this.errorMessage = errorMessage;
  }

  public BaseEntity getLosApplicationData() {
    return losApplicationData;
  }

  public void setLosApplicationData(BaseEntity losApplicationData) {
    this.losApplicationData = losApplicationData;
  }

  public Integer getResponseStatus() {
    return responseStatus;
  }

  public void setResponseStatus(Integer responseStatus) {
    this.responseStatus = responseStatus;
  }

  public ErrorResponseDto getErrorResponseDto() {
    return errorResponseDto;
  }

  public void setErrorResponseDto(ErrorResponseDto errorResponseDto) {
    this.errorResponseDto = errorResponseDto;
  }


}

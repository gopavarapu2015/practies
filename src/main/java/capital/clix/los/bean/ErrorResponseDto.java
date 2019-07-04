package capital.clix.los.bean;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import capital.clix.los.enums.ErrorHandleType;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ErrorResponseDto implements Serializable {

  private int httpStatus;
  private String error;
  private String message;
  private ErrorHandleType errorAction;

  public String getError() {
    return error;
  }

  public void setError(String error) {
    this.error = error;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public int getHttpStatus() {
    return httpStatus;
  }

  public void setHttpStatus(int httpStatus) {
    this.httpStatus = httpStatus;
  }

  public ErrorHandleType getErrorAction() {
    return errorAction;
  }

  public void setErrorAction(ErrorHandleType errorAction) {
    this.errorAction = errorAction;
  }

  @Override
  public String toString() {
    return "ErrorResponseDto{" + "responseStatus=" + httpStatus + ", error='" + error + '\''
        + ", message='" + message + '\'' + '}';
  }
}

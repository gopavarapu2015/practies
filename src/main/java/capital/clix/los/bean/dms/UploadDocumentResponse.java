package capital.clix.los.bean.dms;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import capital.clix.los.bean.ErrorResponseDto;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UploadDocumentResponse {

  private String documentId;

  private String externalDocumentId;

  private boolean success;

  private ErrorResponseDto errorResponseDto;

  public String getDocumentId() {
    return documentId;
  }

  public void setDocumentId(String documentId) {
    this.documentId = documentId;
  }

  public String getExternalDocumentId() {
    return externalDocumentId;
  }

  public void setExternalDocumentId(String externalDocumentId) {
    this.externalDocumentId = externalDocumentId;
  }

  public boolean isSuccess() {
    return success;
  }

  public void setSuccess(boolean success) {
    this.success = success;
  }

  public ErrorResponseDto getErrorResponseDto() {
    return errorResponseDto;
  }

  public void setErrorResponseDto(ErrorResponseDto errorResponseDto) {
    this.errorResponseDto = errorResponseDto;
  }

  @Override
  public String toString() {
    return "UploadDocumentResponse [documentId=" + documentId + ", externalDocumentId="
        + externalDocumentId + ", success=" + success + ", errorResponseDto=" + errorResponseDto
        + "]";
  }


}

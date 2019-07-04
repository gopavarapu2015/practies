package capital.clix.los.bean.cibil;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import capital.clix.los.bean.ErrorResponseDto;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = Include.NON_NULL)
public class ReportFetchResponse {

  private ErrorResponseDto errorResponseDto;
  private boolean success;
  private List<SmeReportFetchResponse> li;

  public ErrorResponseDto getErrorResponseDto() {
    return errorResponseDto;
  }

  public void setErrorResponseDto(ErrorResponseDto errorResponseDto) {
    this.errorResponseDto = errorResponseDto;
  }

  public boolean isSuccess() {
    return success;
  }

  public void setSuccess(boolean success) {
    this.success = success;
  }

  public List<SmeReportFetchResponse> getLi() {
    return li;
  }

  public void setLi(List<SmeReportFetchResponse> li) {
    this.li = li;
  }


}

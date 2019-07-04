package capital.clix.los.bean;

import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class FieldsSearchResponse extends BaseResponse {

  private boolean success;
  private String status;
  private List<Map> dataList;

  public boolean isSuccess() {
    return success;
  }

  public void setSuccess(boolean success) {
    this.success = success;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public List<Map> getDataList() {
    return dataList;
  }

  public void setDataList(List<Map> dataList) {
    this.dataList = dataList;
  }



}

package capital.clix.los.bean.pb;

import java.util.List;
import java.util.Map;
import org.springframework.data.couchbase.core.mapping.Document;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import capital.clix.los.bean.BaseEntity;

@Document
@JsonIgnoreProperties(ignoreUnknown = true)
public class PBExcelDataApplication extends BaseEntity {

  private Map<Integer, List<String>> pbExcelDataApplicationIdListMap;

  public Map<Integer, List<String>> getPbExcelDataApplicationIdListMap() {
    return pbExcelDataApplicationIdListMap;
  }

  public void setPbExcelDataApplicationIdListMap(
      Map<Integer, List<String>> pbExcelDataApplicationIdListMap) {
    this.pbExcelDataApplicationIdListMap = pbExcelDataApplicationIdListMap;
  }

  @Override
  public String toString() {
    return "PBExcelDataApplication [pbExcelDataApplicationIdListMap="
        + pbExcelDataApplicationIdListMap + "]";
  }



}

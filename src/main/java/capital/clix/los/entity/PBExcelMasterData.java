package capital.clix.los.entity;

import java.util.List;
import java.util.Map;
import org.springframework.data.couchbase.core.mapping.Document;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import capital.clix.los.bean.BaseEntity;

@Document
@JsonIgnoreProperties(ignoreUnknown = true)
public class PBExcelMasterData extends BaseEntity {

  private List<String> columnList;
  private Map<String, List<Field>> sourceFieldsListMap;


  public List<String> getColumnList() {
    return columnList;
  }

  public void setColumnList(List<String> columnList) {
    this.columnList = columnList;
  }

  public Map<String, List<Field>> getSourceFieldsListMap() {
    return sourceFieldsListMap;
  }

  public void setSourceFieldsListMap(Map<String, List<Field>> sourceFieldsListMap) {
    this.sourceFieldsListMap = sourceFieldsListMap;
  }

  @Override
  public String toString() {
    return "PBExcelMasterData [columnList=" + columnList + ", sourceFieldsListMap="
        + sourceFieldsListMap + "]";
  }

}

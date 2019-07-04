package capital.clix.los.bean.sme;

import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import capital.clix.los.bean.BaseEntity;
import capital.clix.los.enums.ReportType;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SmeStageReportMapping extends BaseEntity {

  private Map<String, List<ReportType>> stageReportMap;

  public Map<String, List<ReportType>> getStageReportMap() {
    return stageReportMap;
  }

  public void setStageReportMap(Map<String, List<ReportType>> stageReportMap) {
    this.stageReportMap = stageReportMap;
  }


}

package capital.clix.los.bean.pb;

import java.util.HashMap;
import org.springframework.data.couchbase.core.mapping.Document;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import capital.clix.los.bean.BaseEntity;

@Document
@JsonIgnoreProperties(ignoreUnknown = true)
public class PbDelphiReport extends BaseEntity {

  private HashMap<String, Object> delphiReport;

  public HashMap<String, Object> getDelphiReport() {
    return delphiReport;
  }

  public void setDelphiReport(HashMap<String, Object> delphiReport) {
    this.delphiReport = delphiReport;
  }

  @Override
  public String toString() {
    return "PbDelphiReport [delphiReport=" + delphiReport + "]";
  }

}

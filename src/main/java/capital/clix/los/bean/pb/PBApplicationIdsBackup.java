package capital.clix.los.bean.pb;

import java.util.List;
import org.springframework.data.couchbase.core.mapping.Document;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import capital.clix.los.bean.BaseEntity;

@Document
@JsonIgnoreProperties(ignoreUnknown = true)
public class PBApplicationIdsBackup extends BaseEntity {

  private List<String> applicationIds;

  public List<String> getApplicationIds() {
    return applicationIds;
  }

  public void setApplicationIds(List<String> applicationIds) {
    this.applicationIds = applicationIds;
  }



}

package capital.clix.los.entity;

import java.util.List;
import org.springframework.data.couchbase.core.mapping.Document;
import com.couchbase.client.java.repository.annotation.Field;
import com.couchbase.client.java.repository.annotation.Id;
import capital.clix.bean.MasterProperty;

@Document
public class ConfigProperty {

  @Field
  private List<MasterProperty> properties;

  @Id
  String id;

  public void setId(String id) {
    this.id = ConfigProperty.class.getSimpleName();
  }

  public List<MasterProperty> getProperties() {
    return properties;
  }

  public void setProperties(List<MasterProperty> properties) {
    this.properties = properties;
  }
}

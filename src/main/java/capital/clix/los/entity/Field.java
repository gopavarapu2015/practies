package capital.clix.los.entity;

import java.io.Serializable;

public class Field implements Serializable {

  private String name;
  private String path;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }
}

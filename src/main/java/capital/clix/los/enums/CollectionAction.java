package capital.clix.los.enums;

public enum CollectionAction {
  ALL("\\*"), MATCH("\\?"), GET("^\\d+$");

  String pattern;

  CollectionAction(String pattern) {
    this.pattern = pattern;
  }

  public String getPattern() {
    return pattern;
  }

  public static CollectionAction fromString(String name) {
    if (name != null) {
      for (CollectionAction action : values()) {
        if (name.matches(action.getPattern())) {
          return action;
        }
      }
    }
    return null;
  }
}

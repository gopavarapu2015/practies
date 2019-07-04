package capital.clix.los.constants;

public class VaultConstants {

  private VaultConstants() {
    throw new IllegalStateException("Utility class");
  }

  public static final String VAULT_COUCHBASE_DB_URL = "db_url";
  public static final String VAULT_COUCHBASE_DB_BUCKET = "db_bucket";
  public static final String VAULT_COUCHBASE_DB_BUCKET_PASSWORD = "db_bucket_password";
  public static final String VAULT_VALUE = "value";

}

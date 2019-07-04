package capital.clix.los.enums;

import org.apache.http.HttpStatus;

public enum ErrorState {

  INVALID_REQUEST("invalid_request", HttpStatus.SC_BAD_REQUEST),
  INVALID_SOAP_REQUEST("Malformed SOAP request", HttpStatus.SC_INTERNAL_SERVER_ERROR),
  LOAN_CREATION_FAILED("Unable to create loan at indus", HttpStatus.SC_BAD_GATEWAY),
  GUID_ERROR("GUID not found", HttpStatus.SC_BAD_REQUEST),
  INVALID_CLIENT("invalid_client", HttpStatus.SC_BAD_REQUEST),
  INVALID_GRANT("invalid_grant", HttpStatus.SC_BAD_REQUEST),
  UNAUTHORIZED_CLIENT("unauthorized_client", HttpStatus.SC_UNAUTHORIZED),
  INVALID_TOKEN("invalid_token", HttpStatus.SC_GONE),
  EXPIRED_TOKEN("expired_token", HttpStatus.SC_GONE),
  ACCESS_DENIED("access_denied", HttpStatus.SC_FORBIDDEN),
  UNKNOWN_ERROR("unknown_error", HttpStatus.SC_INTERNAL_SERVER_ERROR),
  INTERNAL_SERVER_ERROR("internal_server_error", HttpStatus.SC_INTERNAL_SERVER_ERROR),
  UNAUTHORIZED_ACCESS("unauthorized_access", HttpStatus.SC_UNAUTHORIZED),
  INVALID_COLLECTION_ACTION("Collection action for operand is invalid", HttpStatus.SC_INTERNAL_SERVER_ERROR),
  CACHE_ERROR("Unable to create internal cache", HttpStatus.SC_INTERNAL_SERVER_ERROR),
  JSON_PARSE_ERROR("Unable to parse json", HttpStatus.SC_INTERNAL_SERVER_ERROR);

  private String code;
  private int errorStatus;

  private ErrorState(String code, int errorStatus) {
    this.code = code;
    this.errorStatus = errorStatus;
  }

  public int getErrorStatus() {
    return errorStatus;
  }

  public String getCode() {
    return code;
  }
}

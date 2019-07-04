package capital.clix.los.exception;

import capital.clix.los.enums.ErrorState;

public class LosException extends RuntimeException {

  private final ErrorState errorCode;

  private final String message;

  public LosException(ErrorState errorCode, String message) {
    super(message);
    this.errorCode = errorCode;
    this.message = message;
  }

}

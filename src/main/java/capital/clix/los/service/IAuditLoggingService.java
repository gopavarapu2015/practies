package capital.clix.los.service;

public interface IAuditLoggingService {

  public void auditLogging(String loanApplicationId, String stage, String requestData);



}

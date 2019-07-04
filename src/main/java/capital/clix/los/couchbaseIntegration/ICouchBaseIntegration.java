package capital.clix.los.couchbaseIntegration;

import java.util.List;
import java.util.Map;
import com.couchbase.client.java.query.N1qlQueryRow;
import capital.clix.los.bean.BaseEntity;
import capital.clix.los.bean.BaseResponse;
import capital.clix.los.bean.PersonalLoanEntity;
import capital.clix.los.bean.bl.ApplicationSearch;
import capital.clix.los.enums.AuditDataKey;

public interface ICouchBaseIntegration {

  public <T extends BaseEntity> T getEntity(String id, Class<T> type);

  public void saveLoanEntity(BaseEntity request, BaseResponse response);

  public <T extends BaseEntity> T getApplicationEntity(String id, Class<T> type);

  public void updateLoanEntity(BaseEntity request);

  public PersonalLoanEntity getLoanBasedOnBranch(String branch);

  public N1qlQueryRow getEntityType(String id);

  public List<N1qlQueryRow> searchApplication(ApplicationSearch applicationSearch);

  <T> T getEntityById(String id, Class<T> type);

  public N1qlQueryRow getSingleField(String id, String fieldName);

  public void saveAuditTrailEntity(Map<AuditDataKey, String> auditData);

  public List<N1qlQueryRow> searchBLApplication(ApplicationSearch applicationSearch);

  public String fetchData(String id);
}

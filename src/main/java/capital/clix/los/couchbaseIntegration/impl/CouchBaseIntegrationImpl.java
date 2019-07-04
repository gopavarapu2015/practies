package capital.clix.los.couchbaseIntegration.impl;

import static capital.clix.los.constants.ApplicationConstant.COUCHBASE_KEY_SEPARATOR;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.couchbase.core.CouchbaseTemplate;
import org.springframework.stereotype.Service;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.query.N1qlParams;
import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.N1qlQueryResult;
import com.couchbase.client.java.query.N1qlQueryRow;
import com.couchbase.client.java.query.consistency.ScanConsistency;
import capital.clix.cache.ConfigUtil;
import capital.clix.los.audit.AuditTrailAccessor;
import capital.clix.los.bean.BaseEntity;
import capital.clix.los.bean.BaseResponse;
import capital.clix.los.bean.PersonalLoanEntity;
import capital.clix.los.bean.bl.ApplicationSearch;
import capital.clix.los.couchbaseIntegration.ICouchBaseIntegration;
import capital.clix.los.entity.AuditTrail;
import capital.clix.los.enums.AuditDataKey;
import capital.clix.los.enums.Prefix;
import capital.clix.los.enums.Property;


@Service("couchBaseIntegrationImpl")
public class CouchBaseIntegrationImpl implements ICouchBaseIntegration {

  @Autowired
  private CouchbaseTemplate template;

  @Override
  public void saveLoanEntity(BaseEntity request, BaseResponse response) {
    template.insert(request);
  }

  @Override
  public <T extends BaseEntity> T getApplicationEntity(String id, Class<T> type) {
    return template.findById(Prefix.APPLICATION.getCode() + id, type);
  }

  @Override
  public N1qlQueryRow getEntityType(String id) {

    N1qlQueryResult result = template.queryN1QL(N1qlQuery
        .simple("SELECT type FROM " + ConfigUtil.getPropertyValue(Property.LOS_BUCKET, String.class)
            + " use keys '" + Prefix.APPLICATION.getCode() + id + "'"));

    N1qlQueryRow row = result.allRows().get(0);
    JsonObject obj = row.value();
    return row;
  }

  @Override
  public List<N1qlQueryRow> searchApplication(ApplicationSearch applicationSearch) {


    StringBuffer query = new StringBuffer(
        "SELECT META().id AS _ID,registeredName as borrowerName ,coApplicant[0].firstName as coApplicant,regAddress.city as city,loanDetails.appliedAmount as loanAmount,loanDetails.tenure as loanPeriod,status,created as createdDate FROM "
            + ConfigUtil.getPropertyValue(Property.LOS_BUCKET, String.class));
    StringBuffer whereQuery = new StringBuffer("");
    StringBuffer sortQuery = new StringBuffer("");

    String where = "";
    String sort = "";
    String pagination = "";


    String finalQuery = null;

    Map<String, String> paramMap = applicationSearch.getFilter();
    if (paramMap != null && !paramMap.isEmpty()) {

      whereQuery.append(" where ");
      paramMap.forEach((k, v) -> {

        whereQuery.append(k).append(" = '").append(v).append("' and ");


      });
      int lastIndex = whereQuery.lastIndexOf("and");
      where = whereQuery.substring(0, lastIndex);
    }

    sortQuery.append(" order by created desc , ");
    List<Map<String, String>> sortFeildMap = applicationSearch.getSort();
    if (sortFeildMap != null && !sortFeildMap.isEmpty()) {


      for (Map<String, String> sortMap : sortFeildMap) {
        sortQuery.append(sortMap.get("field")).append(" ").append(sortMap.get("dir")).append(",");
      }

    }
    int lastIndexComma = sortQuery.lastIndexOf(",");
    sort = sortQuery.substring(0, lastIndexComma);

    if (!StringUtils.isBlank(applicationSearch.getPageNo())
        && !StringUtils.isBlank(applicationSearch.getPageSize())) {

      int pageNo = Integer.valueOf(applicationSearch.getPageNo());
      int pageSize = Integer.valueOf(applicationSearch.getPageSize());

      int offset = pageNo * pageSize;
      pagination = " OFFSET " + offset + " LIMIT " + pageSize;
    }
    finalQuery = query.toString() + where + sort + pagination;
    System.out.println("Search Query formed is :--" + finalQuery);
    N1qlQueryResult result = template.queryN1QL(N1qlQuery.simple(finalQuery));

    List<N1qlQueryRow> rowList = result.allRows();

    return rowList;
  }

  @Override
  public List<N1qlQueryRow> searchBLApplication(ApplicationSearch applicationSearch) {


    StringBuffer query = new StringBuffer(
        "SELECT META().id AS _ID,registeredName as borrowerName ,coApplicant[0].firstName as coApplicant,regAddress.city as city,loanDetails.appliedAmount as loanAmount,loanDetails.tenure as loanPeriod,status,created as createdDate FROM "
            + ConfigUtil.getPropertyValue(Property.LOS_BUCKET, String.class));
    StringBuffer whereQuery = new StringBuffer("");
    StringBuffer sortQuery = new StringBuffer("");

    String where = "";
    String sort = "";
    String pagination = "";


    String finalQuery = null;
    whereQuery.append(" where ");
    Map<String, String> paramMap = applicationSearch.getFilter();
    if (paramMap != null && !paramMap.isEmpty()) {
      String value = paramMap.get("registeredName");
      if (!StringUtils.isBlank(value)) {
        whereQuery.append("(LOWER(registeredName)").append(" LIKE LOWER('%").append(value)
            .append("%') or LOWER(META().id) LIKE LOWER('%").append(value).append("%')) and");
      }
      String entityType = paramMap.get("entityType");
      String ownerCode = paramMap.get("ownerCode");
      if (!StringUtils.isBlank(entityType)) {
        whereQuery.append(" entityType = '").append(entityType).append("' and ");
      }
      if (!StringUtils.isBlank(ownerCode)) {
        whereQuery.append(" ownerCode = '").append(ownerCode).append("' and ");
      }
      int lastIndex = whereQuery.lastIndexOf("and");
      where = whereQuery.substring(0, lastIndex);
    }

    sortQuery.append(" order by created desc , ");
    List<Map<String, String>> sortFeildMap = applicationSearch.getSort();
    if (sortFeildMap != null && !sortFeildMap.isEmpty()) {


      for (Map<String, String> sortMap : sortFeildMap) {
        sortQuery.append(sortMap.get("field")).append(" ").append(sortMap.get("dir")).append(",");
      }

    }
    int lastIndexComma = sortQuery.lastIndexOf(",");
    sort = sortQuery.substring(0, lastIndexComma);

    if (!StringUtils.isBlank(applicationSearch.getPageNo())
        && !StringUtils.isBlank(applicationSearch.getPageSize())) {

      int pageNo = Integer.valueOf(applicationSearch.getPageNo());
      int pageSize = Integer.valueOf(applicationSearch.getPageSize());

      int offset = pageNo * pageSize;
      pagination = " OFFSET " + offset + " LIMIT " + pageSize;
    }
    finalQuery = query.toString() + where + sort + pagination;
    System.out.println("Search Query formed is :--" + finalQuery);
    N1qlQueryResult result = template.queryN1QL(N1qlQuery.simple(finalQuery));

    List<N1qlQueryRow> rowList = result.allRows();

    return rowList;
  }


  @Override
  public void updateLoanEntity(BaseEntity request) {

    template.update(request);

  }

  @Override
  public PersonalLoanEntity getLoanBasedOnBranch(String id) {

    N1qlParams params = N1qlParams.build().adhoc(false).consistency(ScanConsistency.STATEMENT_PLUS);
    JsonObject values = JsonObject.create().put("id", id);
    N1qlQuery query = N1qlQuery.parameterized(
        "select LOS.*,META().id AS _ID, META().cas AS _CAS,type from `LOS` where id=$clixSourcingBranch",
        values, params);
    List<PersonalLoanEntity> carts = template.findByN1QL(query, PersonalLoanEntity.class);
    N1qlQueryResult res = template.getCouchbaseBucket().query(query);
    N1qlQueryResult result = template.queryN1QL(N1qlQuery.simple("SELECT * FROM test"));
    // N1qlQuery airlineQuery = N1qlQuery.simple("SELECT `travel-sample`.* FROM `travel-sample`
    // WHERE name=\"United Airlines\" AND type=\"airline\"");
    // N1qlQueryResult queryResult = bucket.query(airlineQuery);
    //
    // for (N1qlQueryRow result: queryResult) {
    // System.out.println(result.value());
    // }
    //
    // String query = "SELECT name FROM `travel-sample` " + "WHERE type = 'airport' LIMIT 100";
    // N1qlQueryResult result1 = bucket.query(N1qlQuery.simple(query));

    // N1qlQueryRow row = result1.allRows().get(0);
    // JsonObject rowJson = row.value();
    // System.out.println("Name in First Row " + rowJson.get("name"));

    // @Query("#{#n1ql.selectEntity} WHERE #{#n1ql.filter} AND firstName like $1")

    if (carts != null)
      return carts.get(0);


    return null;
  }

  @Override
  public <T> T getEntityById(String id, Class<T> type) {
    return template.findById(id, type);
  }

  @Override
  public N1qlQueryRow getSingleField(String id, String fieldName) {
    N1qlQueryResult result = template.queryN1QL(N1qlQuery.simple("SELECT " + fieldName + " FROM "
        + ConfigUtil.getPropertyValue(Property.LOS_BUCKET, String.class) + " use keys '" + id
        + "'"));

    N1qlQueryRow row = result.allRows().get(0);
    JsonObject obj = row.value();
    return row;
  }

  @Override
  public void saveAuditTrailEntity(Map<AuditDataKey, String> auditData) {

    String auditTrailId = AuditTrail.class.getSimpleName() + COUCHBASE_KEY_SEPARATOR
        + auditData.get(AuditDataKey.APPLICATION_ID);
    AuditTrail auditTrail = template.findById(auditTrailId, AuditTrail.class);
    auditTrail = AuditTrailAccessor.getAuditTrailEntity(auditData, auditTrail);
    template.save(auditTrail);
  }

  @Override
  public <T extends BaseEntity> T getEntity(String id, Class<T> type) {
    return template.findById(id, type);
  }

  @Override
  public String fetchData(String id) {
    N1qlQueryResult result = template.queryN1QL(N1qlQuery
        .simple("SELECT * FROM " + ConfigUtil.getPropertyValue(Property.LOS_BUCKET, String.class)
            + " use keys '" + id + "'"));

    N1qlQueryRow row = result.allRows().get(0);
    JsonObject obj = row.value();
    return obj.toString();
  }

}

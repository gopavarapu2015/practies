package capital.clix.los.bean.bl;

import java.util.List;
import java.util.Map;
import capital.clix.los.bean.BaseEntity;

public class ApplicationSearch extends BaseEntity {

  private String pageNo;
  private String pageSize;
  private List<Map<String, String>> sort;
  private Map<String, String> filter;

  public String getPageNo() {
    return pageNo;
  }

  public void setPageNo(String pageNo) {
    this.pageNo = pageNo;
  }

  public String getPageSize() {
    return pageSize;
  }

  public void setPageSize(String pageSize) {
    this.pageSize = pageSize;
  }

  public List<Map<String, String>> getSort() {
    return sort;
  }

  public void setSort(List<Map<String, String>> sort) {
    this.sort = sort;
  }

  public Map<String, String> getFilter() {
    return filter;
  }

  public void setFilter(Map<String, String> filter) {
    this.filter = filter;
  }

  @Override
  public String toString() {
    return "ApplicationSearch [pageNo=" + pageNo + ", pageSize=" + pageSize + ", sort=" + sort
        + ", filter=" + filter + "]";
  }



}

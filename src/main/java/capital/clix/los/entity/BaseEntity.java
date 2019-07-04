package capital.clix.los.entity;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import com.couchbase.client.java.repository.annotation.Id;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import capital.clix.los.bean.LosSyncDto;
import capital.clix.los.enums.ApplicationStage;
import capital.clix.los.enums.LoanType;

/**
 * Copyright 2018 CLIX CAPITAL (P) Limited . All Rights Reserved. CLIX CAPITAL
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * @author vishal
 * @version 1.0, 13/12/18
 */

@JsonInclude(value = Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BaseEntity {

  @Id
  String id;

  @CreatedBy
  private String createdBy;

  @LastModifiedBy
  private String updatedBy;

  @CreatedDate
  private Date created;

  @LastModifiedDate
  private Date updated;

  @Version
  private long version;

  private LoanType entityType;

  private LosSyncDto losSyncDto;

  private ApplicationStage status;

  private Map<String, List<Map<String, Object>>> document = new HashMap<>();

  private String source;


  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }

  public String getUpdatedBy() {
    return updatedBy;
  }

  public void setUpdatedBy(String updatedBy) {
    this.updatedBy = updatedBy;
  }

  public Date getCreated() {
    return created;
  }

  public void setCreated(Date created) {
    this.created = created;
  }

  public Date getUpdated() {
    return updated;
  }

  public void setUpdated(Date updated) {
    this.updated = updated;
  }

  public LosSyncDto getLosSyncDto() {
    return losSyncDto;
  }

  public void setLosSyncDto(LosSyncDto losSyncDto) {
    this.losSyncDto = losSyncDto;
  }

  public LoanType getEntityType() {
    return entityType;
  }

  public void setEntityType(LoanType entityType) {
    this.entityType = entityType;
  }

  public ApplicationStage getStatus() {
    return status;
  }

  public void setStatus(ApplicationStage status) {
    this.status = status;
  }

  public Map<String, List<Map<String, Object>>> getDocument() {
    return document;
  }

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }


}

package org.jlab.jaws.persistence.model;

import java.math.BigInteger;
import org.hibernate.envers.RevisionType;

/**
 * @author ryans
 */
public class AuditedEntityChange {
  private final long revision;
  private final RevisionType type;
  private final BigInteger entityId;
  private final String entityName;
  private final Class entityClass;
  private final String classLabel;

  public AuditedEntityChange(
      long revision,
      RevisionType type,
      BigInteger entityId,
      String entityName,
      Class entityClass,
      String classLabel) {
    this.revision = revision;
    this.type = type;
    this.entityId = entityId;
    this.entityName = entityName;
    this.entityClass = entityClass;
    this.classLabel = classLabel;
  }

  public long getRevision() {
    return revision;
  }

  public RevisionType getType() {
    return type;
  }

  public BigInteger getEntityId() {
    return entityId;
  }

  public String getEntityName() {
    return entityName;
  }

  public Class getEntityClass() {
    return entityClass;
  }

  public String getClassLabel() {
    return classLabel;
  }
}

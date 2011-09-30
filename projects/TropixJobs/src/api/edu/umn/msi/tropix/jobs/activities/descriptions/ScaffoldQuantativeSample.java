package edu.umn.msi.tropix.jobs.activities.descriptions;

import java.io.Serializable;

import javax.annotation.Nonnull;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class ScaffoldQuantativeSample implements Serializable {
  @Id
  @Nonnull
  @GeneratedValue(strategy = GenerationType.AUTO)
  private String id;

  private String name;

  private String category;

  private String description;

  @Column(name = "prim")
  private Boolean primary;

  private String reporter;

  public String getId() {
    return id;
  }

  public void setId(final String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(final String category) {
    this.category = category;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(final String description) {
    this.description = description;
  }

  public Boolean isPrimary() {
    return primary;
  }

  public void setPrimary(final Boolean primary) {
    this.primary = primary;
  }

  public String getReporter() {
    return reporter;
  }

  public void setReporter(final String reporter) {
    this.reporter = reporter;
  }

}

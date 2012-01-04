package edu.umn.msi.tropix.models.locations;

import java.util.Date;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class LocationImpl implements Location {
  private final Location parent;
  private final Location root;
  private String type;
  private String name;
  @Nullable
  private String sort;
  private String id;
  @Nullable
  private Date creationDate;
  private boolean folder;

  public LocationImpl(@Nullable final Location parent) {
    this.parent = parent;
    if(parent == null) {
      this.root = this;
    } else {
      this.root = parent.getRoot();
    }
  }
  
  public LocationImpl(@Nullable final Location parent, @Nonnull final Location template) {
    this(parent);
    this.type = template.getType();
    this.name = template.getName();
    this.sort = template.getSort();
    this.id = template.getId();
    this.folder = template.isFolder();
  }

  public Location getParent() {
    return this.parent;
  }

  public Location getRoot() {
    return this.root;
  }

  public String getId() {
    return this.id;
  }

  public String getName() {
    return this.name;
  }

  public String getSort() {
    return sort == null ? name : sort;
  }

  public String getType() {
    return this.type;
  }

  public Date getCreationDate() {
    return this.creationDate;
  }

  public boolean isFolder() {
    return this.folder;
  }

  protected void setId(final String id) {
    this.id = id;
  }

  protected void setName(final String name) {
    this.name = name;
  }

  @Nullable
  protected void setSort(final String sort) {
    this.sort = sort;
  }

  protected void setType(final String type) {
    this.type = type;
  }

  @Nullable
  protected void setCreationDate(final Date creationDate) {
    this.creationDate = creationDate;
  }

  protected void setFolder(final boolean folder) {
    this.folder = folder;
  }

}
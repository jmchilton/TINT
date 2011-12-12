package edu.umn.msi.tropix.models;

import java.io.Serializable;
import java.util.Collection;

public class Tag implements Serializable {
  private String id;
  private String tag;
  private String name;
  private Collection<TropixObject> objects;
  
  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public String getName() {
    return name;
  }
  
  public void setName(final String name) {
    this.name = name;
  }
  
  public String getTag() {
    return tag;
  }
  
  public void setTag(String tag) {
    this.tag = tag;
  }
  
  public void setObjects(Collection<TropixObject> objects) {
    this.objects = objects;
  }
  
  public Collection<TropixObject> getObjects() {
    return objects;
  }
  
  /**
   * Compares <code>obj</code> to it self and returns true if they both are same
   * 
   * @param obj
   **/
  @Override
  public boolean equals(final Object obj) {
    if(obj instanceof Tag) {
      final Tag c = (Tag) obj;
      if(getId() != null && getId().equals(c.getId())) {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns hash code for the primary key of the object
   **/
  @Override
  public int hashCode() {
    if(getId() != null) {
      return getId().hashCode();
    }
    return 0;
  }

  
}

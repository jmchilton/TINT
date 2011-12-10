package edu.umn.msi.tropix.models;

import java.io.Serializable;

public class UserPreference  implements Serializable {
  public String id;
  public String value;
  private UserPreferenceType type;
  
  public UserPreferenceType getType() {
    return type;
  }

  public void setType(final UserPreferenceType type) {
    this.type = type;
  }

  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }
  
  
  /**
   * Compares <code>obj</code> to it self and returns true if they both are same
   * 
   * @param obj
   **/
  @Override
  public boolean equals(final Object obj) {
    if(obj instanceof UserPreferenceType) {
      final UserPreference c = (UserPreference) obj;
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

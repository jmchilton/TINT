package edu.umn.msi.tropix.models.utils;

import java.io.Serializable;

import edu.umn.msi.tropix.models.TropixObject;

public class TropixObjectWithContext<T extends TropixObject> implements Serializable {
  private TropixObjectContext tropixObjectContext;
  private T tropixObject;
  
  public TropixObjectWithContext() {
  }
  
  public TropixObjectWithContext(final TropixObjectContext tropixObjectContext, 
                                 final T tropixObject) {
    this.tropixObjectContext = tropixObjectContext;
    this.tropixObject = tropixObject;
  }

  public TropixObjectContext getTropixObjectContext() {
    return tropixObjectContext;
  }
  
  public void setTropixObjectContext(final TropixObjectContext tropixObjectContext) {
    this.tropixObjectContext = tropixObjectContext;
  }
  
  public T getTropixObject() {
    return tropixObject;
  }
  
  public void setTropixObject(final T tropixObject) {
    this.tropixObject = tropixObject;
  }

}

package edu.umn.msi.tropix.models.utils;

import java.io.Serializable;

import javax.annotation.Nonnull;

import com.google.common.base.Preconditions;

import edu.umn.msi.tropix.models.TropixObject;

@SuppressWarnings("serial")
public class TropixObjectContext<T extends TropixObject> implements Serializable {
  private TropixObjectUserAuthorities tropixObjectContext;
  private T tropixObject;

  public TropixObjectContext() {
  }

  public TropixObjectContext(@Nonnull final TropixObjectUserAuthorities tropixObjectContext,
      @Nonnull final T tropixObject) {
    Preconditions.checkNotNull(tropixObjectContext);
    this.tropixObjectContext = tropixObjectContext;
    this.tropixObject = tropixObject;
  }

  public TropixObjectContext(@Nonnull final T object) {
    this.tropixObjectContext = null;
    this.tropixObject = object;
  }

  public TropixObjectUserAuthorities getTropixObjectContext() {
    return tropixObjectContext;
  }

  public T getTropixObject() {
    return tropixObject;
  }

  public void setTropixObject(final T tropixObject) {
    this.tropixObject = tropixObject;
  }

}

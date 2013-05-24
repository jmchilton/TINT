package edu.umn.msi.tropix.persistence.service;

import edu.umn.msi.tropix.models.TropixObject;
import edu.umn.msi.tropix.persistence.aop.AutoUser;
import edu.umn.msi.tropix.persistence.aop.PersistenceMethod;
import edu.umn.msi.tropix.persistence.aop.UserId;

public interface TropixObjectPathLoader {

  @PersistenceMethod
  public abstract TropixObject getPath(@AutoUser @UserId final String userGridId, final String[] names);

}
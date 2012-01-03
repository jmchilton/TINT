package edu.umn.msi.tropix.models.locations;

import edu.umn.msi.tropix.models.TropixObject;
import edu.umn.msi.tropix.models.utils.TropixObjectUserAuthorities;

public interface TropixObjectLocation {

  TropixObjectLocation getTropixObjectLocationRoot();

  TropixObject getObject();

  TropixObjectUserAuthorities getContext();

}

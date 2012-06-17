package edu.umn.msi.tropix.common.test;

import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

@ManagedResource
public class ManagedTestBean {

  @ManagedAttribute
  public String getSeven() {
    return "7";
  }
}

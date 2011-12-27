package edu.umn.msi.tropix.storage.service.impl;

public class TransportSecurity {

  public static String getPath(final String configDirectory) {
    System.out.println("ConfigDirectory is " + configDirectory);
    return "cherry.jks";
  }
  
}

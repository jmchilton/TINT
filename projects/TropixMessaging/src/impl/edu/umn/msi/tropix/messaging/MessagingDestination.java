package edu.umn.msi.tropix.messaging;

public class MessagingDestination {
  
  public static final String PREFIX = "activemq:queue:"; 
  
  public static String withName(final String name) {
    return PREFIX + name;
  }
  
}

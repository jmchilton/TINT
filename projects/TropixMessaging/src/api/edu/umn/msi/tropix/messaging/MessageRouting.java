package edu.umn.msi.tropix.messaging;

public class MessageRouting {
  
  public static final String QUEUE_PREFIX = "activemq:queue:"; 
  
  public static String withName(final String name) {
    return QUEUE_PREFIX + name;
  }
  
}

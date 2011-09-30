package edu.umn.msi.tropix.messaging;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class RunBroker {
  
  public static void main(final String[] args) throws InterruptedException {
    final ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("edu/umn/msi/tropix/messaging/context.xml");
    Thread.sleep(1000 * 60 * 60);
  }
  
}

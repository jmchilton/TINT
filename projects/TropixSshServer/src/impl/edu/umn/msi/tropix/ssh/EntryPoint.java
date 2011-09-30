package edu.umn.msi.tropix.ssh;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class EntryPoint {

  public static void main(final String[] args) throws InterruptedException {
    final ClassPathXmlApplicationContext context = 
      new ClassPathXmlApplicationContext("edu/umn/msi/tropix/ssh/context.xml");
    final SshServerWrapper server = context.getBean(SshServerWrapper.class);
    server.start();
    try {
      while(true) {
        Thread.sleep(1000);
      }
    } finally {
      server.stop();
      context.destroy();
    }
  }
  
}

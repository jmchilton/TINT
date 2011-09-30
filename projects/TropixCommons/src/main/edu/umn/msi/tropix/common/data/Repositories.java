package edu.umn.msi.tropix.common.data;

import org.springframework.context.support.ClassPathXmlApplicationContext;


public class Repositories {

  // Not an ideal to retrieve a repository, but it should work with tropix
  // config setup
  public static Repository getInstance() {    
    final String contextContextLocation = Repositories.class.getPackage().getName().replaceAll("\\.", "/") + "/context.xml";
    return new ClassPathXmlApplicationContext(contextContextLocation).getBean(Repository.class);
  }
}

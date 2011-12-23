package edu.umn.msi.tropix.storage.cagrid.service;

import java.rmi.RemoteException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import edu.umn.msi.cagrid.introduce.interfaces.spring.client.Services;
import edu.umn.msi.tropix.storage.cagrid.service.TropixStorageConfiguration;

@SuppressWarnings("all") public class TropixStorageApplicationContext {
  static {
    Services.registerService("edu.umn.msi.tropix.storage.cagrid.TropixStorageImpl");
  }

  private static ApplicationContext applicationContext;

  public static void init(TropixStorageConfiguration serviceConfiguration) throws RemoteException
  {
    if(applicationContext == null)
    {
      try 
      {
        String applicationContextPath = "file:" + serviceConfiguration.getApplicationContext();
        applicationContext = new FileSystemXmlApplicationContext(applicationContextPath);
        Services.setApplicationContext(applicationContext);
      }
      catch(Exception e) 
      {
        e.printStackTrace();
        throw new RemoteException("Failed to initialize spring.",e);
      }
    }
  }

  public static ApplicationContext get() 
  {
    return applicationContext;
  }
}



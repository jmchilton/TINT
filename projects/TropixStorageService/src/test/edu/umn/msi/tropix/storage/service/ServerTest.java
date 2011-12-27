package edu.umn.msi.tropix.storage.service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.xml.ws.Endpoint;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.bus.spring.SpringBusFactory;
import org.apache.cxf.endpoint.UpfrontConduitSelector;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.apache.cxf.jaxrs.client.JAXRSClientFactoryBean;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.jaxrs.lifecycle.SingletonResourceProvider;
import org.apache.cxf.transport.Conduit;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.storage.service.client.StorageServiceFactory;
import edu.umn.msi.tropix.storage.service.impl.StorageServiceImpl;

@ContextConfiguration(locations = "testContext.xml")
public class ServerTest extends AbstractTestNGSpringContextTests {
  
  @Inject 
  private StorageServiceFactory storageServiceFactory;
    
  private boolean secure = true;

  @Test
  public void test() throws InterruptedException, HttpException, IOException {

    //SpringBusFactory bf = new SpringBusFactory();
    //URL busFile = getClass().getResource("server.xml");
    //Bus bus = bf.createBus(busFile.toString());
    //BusFactory.setDefaultBus(cxf);

    /*
    JAXRSServerFactoryBean sf = new JAXRSServerFactoryBean();
    sf.setResourceClasses(StorageServiceImpl.class);
    sf.setResourceProvider(StorageServiceImpl.class,
        new SingletonResourceProvider(new StorageServiceImpl()));
    if(secure) {
      sf.setAddress("https://localhost:8743/");
    } else {
      sf.setAddress("http://localhost:8780/");
    }
    sf.create();
    */
    //setupProtocol();
    //httpCall();
    proxyClientCall();

    
    // If Basic Authentication required could use:                                                                                  
    /*                                                                                                                              
    String authorizationHeader = "Basic "                                                                                           
       + org.apache.cxf.common.util.Base64Utility.encode("username:password".getBytes());                                           
    httpget.addRequestHeader("Authorization", authorizationHeader);                                                                 
    */
    /*
    System.out.println("Sending HTTPS GET request to query customer info");
    HttpClient httpclient = new HttpClient();
    GetMethod httpget = new GetMethod("https://localhost:9000/storage/data/123");
    httpget.addRequestHeader("Accept" , "text/xml");

    try {
        httpclient.executeMethod(httpget);
        System.out.println(httpget.getResponseBodyAsString());
    } finally {
        httpget.releaseConnection();
    }
    */

    

    
    //Thread.sleep(1000 * 10);
    
    //Object implementor = new TestImpl();
    //String address = "https://localhost:9001/storage";
    //Endpoint.publish(address, implementor);
  }
  
  private void proxyClientCall() {
    final String address;
    if(secure) {
      address = "https://localhost:8743/";
    } else {
      address = "http://localhost:8780/";
    }

    StorageService testI = storageServiceFactory.get(address);
    //StorageService testI = JAXRSClientFactory.create(address, StorageService.class);
    
    //bean.setBus(cxf);
    //final StorageService testI = (StorageService) bean.create();
    //final TestI testI = JAXRSClientFactory.create("https://localhost:9000", TestI.class, "edu/umn/msi/tropix/storage/service/server.xml");
    
    //bean.setConduitSelector(new UpfrontConduitSelector());
    //bean.setConduitSelector(new UpfrontConduitSelector(conduit));
    //final WebClient wc = bean.createWebClient();
    
    testI.getData("MooCow");    
  }
    
}

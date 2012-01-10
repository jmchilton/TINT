package edu.umn.msi.tropix.storage.service.impl;

import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.lifecycle.SingletonResourceProvider;
import org.springframework.beans.factory.annotation.Value;

import edu.umn.msi.tropix.storage.service.StorageService;

@ManagedBean
public class Server {
  private static final Log LOG = LogFactory.getLog(Server.class);
  private boolean enable;
  private boolean secure;
  private int httpPort, httpsPort;
  private String host;
  private StorageService implementation;

  @Inject
  public void setStorageService(final StorageService implementation) {
    this.implementation = implementation;
  }
  
  @Value("${storage.service.host}")
  public void setHost(final String host) {
    this.host = host;
  }

  @Value("${storage.service.http.port}")
  public void setHttpPort(final int httpPort) {
    this.httpPort = httpPort;
  }

  @Value("${storage.service.https.port}")
  public void setHttpsPort(final int httpsPort) {
    this.httpsPort = httpsPort;
  }

  @Value("${storage.service.enable}")
  public void setEnable(final boolean enable) {
    this.enable = enable;
  } 
  
  @Value("${storage.service.secure}")
  public void setSecure(final boolean secure) {
    this.secure = secure;
  }
  
  @PostConstruct
  public void init() {
    LOG.debug(String.format("Enable storage service - %b", enable)); 
    if(enable) {
      startServer();
    }
  }
  
  private int getPort() { 
    return secure ? httpsPort : httpPort; 
  }
  
  private String getSchema() {
    return secure ? "https" : "http";
  }
  
  private void startServer() {
    final JAXRSServerFactoryBean sf = new JAXRSServerFactoryBean();
    sf.setResourceClasses(implementation.getClass());
    sf.setResourceProvider(implementation.getClass(), new SingletonResourceProvider(implementation));
    final int port = getPort();
    String schema = getSchema();
    final String address = String.format("%s://%s:%s/", schema, host, port);
    LOG.info(String.format("Starting storage service wit address [%s]", address));
    sf.setAddress(address);
    sf.create();
  }

}
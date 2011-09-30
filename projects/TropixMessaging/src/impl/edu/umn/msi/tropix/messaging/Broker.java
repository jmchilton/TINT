package edu.umn.msi.tropix.messaging;

import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.apache.activemq.broker.BrokerService;
import org.springframework.beans.factory.annotation.Value;

@ManagedBean
public class Broker {
  private boolean start;
  private boolean persist;
  private String brokerUrl;
  private BrokerService brokerService;
  
  @Inject
  public Broker(@Value("${messaging.broker.start}") final boolean start,
                @Value("${messaging.broker.url}") final String brokerUrl,
                @Value("${messaging.broker.persist}") final boolean persist) {
    this.start = start;
    this.brokerUrl = brokerUrl;
    this.persist = persist;
  }
  
  @PostConstruct
  public void start() throws Exception {
    if(this.start) {
      this.brokerService = new BrokerService();
      this.brokerService.addConnector(brokerUrl);
      this.brokerService.setPersistent(persist);
      this.brokerService.start();
    }
  }
  
  @PreDestroy 
  public void stop() throws Exception {
    if(this.brokerService != null) {
      this.brokerService.stop();
    }
  }
  
}

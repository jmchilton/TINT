package edu.umn.msi.tropix.messaging.test1;

import org.apache.camel.builder.RouteBuilder;

public class TestRoutes extends RouteBuilder {

  public void configure() throws Exception {
    //from("activemq:queue:numbers").to("bean:foo");    
  }

}

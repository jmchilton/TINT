package edu.umn.msi.tropix.messaging;

import org.apache.camel.Produce;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.messaging.test1.Foo;
import edu.umn.msi.tropix.messaging.test1.Foo.FooMessage;

@ContextConfiguration(locations = "testContext.xml")
public class QueueTest extends AbstractTestNGSpringContextTests {

  private Foo fooProxy;
  
  @Produce(uri = MessageRouting.QUEUE_PREFIX + "numbers")
  public void setFoo(final Foo fooProxy) {
    this.fooProxy = fooProxy;
  }
  
  @Test(groups = "spring")
  public void testQueue() throws InterruptedException {
    assert fooProxy != null;
    final FooMessage in = new FooMessage();
    in.setMoo("4");
    fooProxy.bar(in);
    Thread.sleep(12000);
    //queueSender.send("Moo");
  }
  
}

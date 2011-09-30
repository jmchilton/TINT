package edu.umn.msi.tropix.messaging.test1;

import javax.annotation.ManagedBean;

import org.apache.camel.Consume;

import edu.umn.msi.tropix.messaging.MessageRouting;

@ManagedBean
public class FooImpl implements Foo {

  @Consume(uri=MessageRouting.QUEUE_PREFIX + "numbers")
  public void bar(final FooMessage in) {
    FooMessage out = new FooMessage();
    out.setCow(Integer.toString(Integer.parseInt(in.getMoo()) + 5));
    System.out.println(out.getCow());
  }

}

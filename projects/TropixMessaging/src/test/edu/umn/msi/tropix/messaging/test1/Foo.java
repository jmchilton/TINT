package edu.umn.msi.tropix.messaging.test1;

import java.io.Serializable;

import edu.umn.msi.tropix.messaging.AsyncMessage;

public interface Foo {

  public class FooMessage implements Serializable {
    private String moo;

    private String cow;

    public String getMoo() {
      return moo;
    }

    public void setMoo(final String moo) {
      this.moo = moo;
    }

    public String getCow() {
      return cow;
    }

    public void setCow(final String cow) {
      this.cow = cow;
    }

  }

  @AsyncMessage
  void bar(final FooMessage input);

}

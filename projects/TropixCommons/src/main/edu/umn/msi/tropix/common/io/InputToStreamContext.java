package edu.umn.msi.tropix.common.io;

import java.io.OutputStream;

import javax.annotation.WillNotClose;

public interface InputToStreamContext {

  /**
   * The InputContext is required to not close the supplied OutputStream.
   * 
   * @param outputStream {@link OutputStream} to write data to.
   */
  void get(@WillNotClose OutputStream outputStream);

}

package edu.umn.msi.tropix.common.io;

import java.io.InputStream;

import javax.annotation.WillNotClose;

public interface OutputToStreamContext {

  /**
   * Populates the contents of the resource defined by this OutputContext with the 
   * contents of the supplied InputStream. This OutputContext will not close the InputStream.
   * 
   * @param inputStream
   */
  void put(@WillNotClose InputStream inputStream);
  
}

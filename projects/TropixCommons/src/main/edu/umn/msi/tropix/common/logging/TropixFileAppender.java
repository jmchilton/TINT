package edu.umn.msi.tropix.common.logging;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.FileAppender;

/**
 * Stock file appender requires file destination directory to exist before
 * it Java starts up. This circumvents that by creating the directory if it 
 * does not exist. 
 * 
 * @author John Chilton (jmchilton at gmail dot com)
 *
 */
public class TropixFileAppender  extends FileAppender {
  
  @Override
  public synchronized void setFile(final String fileName, 
                                   final boolean append,
                                   final boolean bufferedIO, 
                                   final int bufferSize) throws IOException {
    final File file = new File(fileName);
    file.getParentFile().mkdirs();
    super.setFile(fileName, append, bufferedIO, bufferSize);
  }

}

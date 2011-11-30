package edu.umn.msi.tropix.common.io;

import java.io.File;
import java.io.Reader;
import java.io.Writer;
import java.util.Properties;

import javax.annotation.WillClose;
import javax.annotation.WillNotClose;

/**
 * Utility interface for working with properties files without dealing
 * with checked exceptions. 
 * 
 * @author John Chilton
 *
 */
public interface PropertiesUtils {
  Properties load(final String propertiesAsString);
  
  Properties load(@WillClose final Reader reader);
  
  Properties load(final File file);

  String toString(Properties properties);
  
  void store(Properties properties, @WillNotClose Writer writer);
}
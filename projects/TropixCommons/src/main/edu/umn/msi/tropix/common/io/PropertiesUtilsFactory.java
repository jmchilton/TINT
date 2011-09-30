package edu.umn.msi.tropix.common.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.Properties;

import javax.annotation.WillClose;

/**
 * Utility class for obtaining the default instance of {@link PropertyUtils}.
 * 
 * @author John Chilton
 *
 */
public class PropertiesUtilsFactory {
  private static final PropertiesUtils DEFAULT_INSTANCE = new PropertiesUtilsImpl();
  
  public static PropertiesUtils getInstance() {
    return DEFAULT_INSTANCE;
  }
  
  private static class PropertiesUtilsImpl implements PropertiesUtils {
    private static final IOUtils IO_UTILS = IOUtilsFactory.getInstance();
    
    public Properties load(final String propertiesAsString) {
      return load(new ByteArrayInputStream(propertiesAsString.getBytes()));
    }

    public Properties load(@WillClose final InputStream inputStream) {
      final Properties properties = new Properties();
      try {
        properties.load(inputStream);
      } catch(IOException e) {
        throw new IORuntimeException(e);
      } finally {
        IO_UTILS.closeQuietly(inputStream);
      }
      return properties;
    }
    
    public Properties load(@WillClose final Reader reader) {
      try {
        final String contents = IO_UTILS.toString(reader);
        return load(contents);
      } finally {
        IO_UTILS.closeQuietly(reader);
      }
    }

    public String toString(final Properties properties) {
      final ByteArrayOutputStream stream = new ByteArrayOutputStream();
      store(properties, stream);
      return new String(stream.toByteArray());
    }

    public void store(final Properties properties, final OutputStream outputStream) {
      try {
        properties.store(outputStream, null);
      } catch(IOException e) {
        throw new IORuntimeException(e);
      }
    }
    
    public void store(final Properties properties, final Writer writer) {
      final String contents = toString(properties);
      IO_UTILS.append(writer, contents);        
    }
    
  }
  
}

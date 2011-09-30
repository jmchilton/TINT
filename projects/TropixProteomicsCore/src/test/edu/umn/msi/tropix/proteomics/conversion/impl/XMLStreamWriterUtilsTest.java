package edu.umn.msi.tropix.proteomics.conversion.impl;

import java.io.ByteArrayOutputStream;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.io.IOUtils;
import edu.umn.msi.tropix.common.io.IOUtilsFactory;

public class XMLStreamWriterUtilsTest {
  private static final IOUtils IO_UTILS = IOUtilsFactory.getInstance();
  
  private XMLStreamWriter getClosedWriter() {
    final ByteArrayOutputStream stream = new ByteArrayOutputStream();
    final XMLStreamWriter writer =  XMLStreamWriterUtils.get(stream, "http://schema");
    IO_UTILS.closeQuietly(stream);
    try {
      writer.close();
    } catch(XMLStreamException e) {
      throw new RuntimeException(e);
    }
    return writer;
  }
  
  @Test(groups = "unit", expectedExceptions = RuntimeException.class)
  public void testWriteDefaultNamespaceException() {
    XMLStreamWriterUtils.writeDefaultNamespace(getClosedWriter(), "http://foo");
  }

  @Test(groups = "unit", expectedExceptions = RuntimeException.class)
  public void testWriterCharactersException() {
     XMLStreamWriterUtils.writeCharacters(getClosedWriter(), "foo");
  }


  @Test(groups = "unit", expectedExceptions = RuntimeException.class)
  public void testFlushException() {
     XMLStreamWriterUtils.flush(getClosedWriter());
  }

  @Test(groups = "unit", expectedExceptions = RuntimeException.class)
  public void testStartException() {
     XMLStreamWriterUtils.writeStartElement(getClosedWriter(), "foo");
  }

  @Test(groups = "unit", expectedExceptions = RuntimeException.class)
  public void testWriteAttributeException() {
     XMLStreamWriterUtils.writeAttribute(getClosedWriter(), "foo", "cow");
  }

  @Test(groups = "unit", expectedExceptions = RuntimeException.class)
  public void testEndException() {
     XMLStreamWriterUtils.writeEndElement(getClosedWriter());
  }
    
}

package edu.umn.msi.tropix.proteomics.conversion.impl;

import javax.xml.stream.XMLStreamException;

/**
 * {@link RuntimeException} that simply wraps an {@link XMLStreamException}.
 * 
 * @author John Chilton
 * 
 */
public class XMLStreamRuntimeException extends RuntimeException {

  public XMLStreamRuntimeException(final XMLStreamException e) {
    super(e);
  }

}

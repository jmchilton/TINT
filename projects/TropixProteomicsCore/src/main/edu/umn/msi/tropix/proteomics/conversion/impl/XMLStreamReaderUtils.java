/********************************************************************************
 * Copyright (c) 2009 Regents of the University of Minnesota
 *
 * This Software was written at the Minnesota Supercomputing Institute
 * http://msi.umn.edu
 *
 * All rights reserved. The following statement of license applies
 * only to this file, and and not to the other files distributed with it
 * or derived therefrom.  This file is made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Minnesota Supercomputing Institute - initial API and implementation
 *******************************************************************************/

package edu.umn.msi.tropix.proteomics.conversion.impl;

import java.io.InputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class XMLStreamReaderUtils {
  public static boolean hasNext(final XMLStreamReader reader) {
    try {
      return reader.hasNext();
    } catch(final XMLStreamException e) {
      throw new XMLStreamRuntimeException(e);
    }
  }

  /**
   * Fetches the attribute name from the current elements namespace.
   * 
   * @param reader
   *          active pull parser to read XML from
   * @param name
   *          name of the attribute to read the value of
   * @return the value of attribute {@code attribute}
   */
  public static String getAttributeValue(final XMLStreamReader reader, final String name) {
    return reader.getAttributeValue(null, name);
  }

  public static int next(final XMLStreamReader reader) {
    try {
      return reader.next();
    } catch(final XMLStreamException e) {
      throw new XMLStreamRuntimeException(e);
    }
  }

  public static XMLStreamReader get(final InputStream inputStream) {
    try {
      return XMLInputFactory.newInstance().createXMLStreamReader(inputStream);
    } catch(final XMLStreamException e) {
      throw new XMLStreamRuntimeException(e);
    }
  }

  public static boolean isStartOfElement(final XMLStreamReader reader, final String name) {
    return isStartElement(reader) && reader.getName().getLocalPart().equals(name);
  }

  public static boolean isStartElement(final XMLStreamReader reader) {
    return reader.getEventType() == XMLStreamReader.START_ELEMENT;
  }

  public static boolean isEndOfElement(final XMLStreamReader reader, final String name) {
    return reader.getEventType() == XMLStreamReader.END_ELEMENT && reader.getName().getLocalPart().equals(name);
  }

  public static String getNextText(final XMLStreamReader reader) {
    while(!reader.hasText()) {
      next(reader);
    }
    return reader.getText();
  }

  public static String getElementText(final XMLStreamReader reader) {
    try {
      return reader.getElementText();
    } catch(final XMLStreamException e) {
      throw new XMLStreamRuntimeException(e);
    }
  }

  public static boolean skipToElement(final XMLStreamReader reader, final String name) {
    while(XMLStreamReaderUtils.hasNext(reader)) {
      XMLStreamReaderUtils.next(reader);
      if(XMLStreamReaderUtils.isStartOfElement(reader, name)) {
        return true;
      }
    }
    return false;
  }

}

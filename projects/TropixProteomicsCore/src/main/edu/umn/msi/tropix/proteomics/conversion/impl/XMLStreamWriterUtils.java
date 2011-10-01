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

import java.io.OutputStream;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

class XMLStreamWriterUtils {

  public static XMLStreamWriter get(final OutputStream outputStream, final String defaultNamespace) {
    try {
      final XMLStreamWriter xmlStreamWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(outputStream);
      xmlStreamWriter.setDefaultNamespace(defaultNamespace);
      xmlStreamWriter.writeStartDocument();
      return xmlStreamWriter;
    } catch(final XMLStreamException e) {
      throw new XMLStreamRuntimeException(e);
    }
  }

  public static void writeDefaultNamespace(final XMLStreamWriter xmlStreamWriter, final String namespace) {
    try {
      xmlStreamWriter.writeDefaultNamespace(namespace);
    } catch(final XMLStreamException e) {
      throw new XMLStreamRuntimeException(e);
    }
  }

  public static void flush(final XMLStreamWriter xmlStreamWriter) {
    try {
      xmlStreamWriter.flush();
    } catch(final XMLStreamException e) {
      throw new XMLStreamRuntimeException(e);
    }
  }

  public static void writeCharacters(final XMLStreamWriter xmlStreamWriter, final String text) {
    try {
      xmlStreamWriter.writeCharacters(text);
    } catch(final XMLStreamException e) {
      throw new XMLStreamRuntimeException(e);
    }
  }

  public static void writeAttribute(final XMLStreamWriter xmlStreamWriter, final String name, final String value) {
    try {
      xmlStreamWriter.writeAttribute(name, value);
    } catch(final XMLStreamException e) {
      throw new XMLStreamRuntimeException(e);
    }
  }

  public static void writeStartElement(final XMLStreamWriter xmlStreamWriter, final String startElement) {
    try {
      xmlStreamWriter.writeStartElement(startElement);
    } catch(final XMLStreamException e) {
      throw new XMLStreamRuntimeException(e);
    }
  }

  public static void writeEndElement(final XMLStreamWriter xmlStreamWriter) {
    try {
      xmlStreamWriter.writeEndElement();
    } catch(final XMLStreamException e) {
      throw new XMLStreamRuntimeException(e);
    }
  }

}

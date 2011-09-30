package edu.umn.msi.tropix.proteomics.conversion.impl;

import java.io.InputStream;

import javax.annotation.WillClose;
import javax.xml.stream.XMLStreamReader;

public class MzxmlVerifier {

  public static boolean isValid(@WillClose final InputStream inputStream) {
    final XMLStreamReader xmlStream = XMLStreamReaderUtils.get(inputStream);
    try {
      XMLStreamReaderUtils.next(xmlStream);
      if(!xmlStream.getName().getLocalPart().equals("mzXML")) {
        return false;
      }
      while(XMLStreamReaderUtils.hasNext(xmlStream)) {
        XMLStreamReaderUtils.next(xmlStream);
      }
    } catch(XMLStreamRuntimeException e) {
      return false;
    }
    return true;
  }

}

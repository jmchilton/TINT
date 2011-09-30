package edu.umn.msi.tropix.proteomics.xtandem.impl;

import java.io.InputStream;
import java.util.Map;

import javax.annotation.WillClose;
import javax.xml.stream.XMLStreamReader;

import com.google.common.collect.Maps;

import edu.umn.msi.tropix.common.io.IOUtils;
import edu.umn.msi.tropix.common.io.IOUtilsFactory;
import edu.umn.msi.tropix.proteomics.conversion.impl.XMLStreamReaderUtils;

public class BiomlParameterReaderImpl {
  private static final IOUtils IO_UTILS = IOUtilsFactory.getInstance();
  
  public Map<String, String> buildMap(@WillClose final InputStream inputStream) {
    try {
      final Map<String, String> parameters = Maps.newHashMap();
      final XMLStreamReader reader = XMLStreamReaderUtils.get(inputStream);
      populateMap(parameters, reader);
      return parameters;
    } finally {
      IO_UTILS.closeQuietly(inputStream);
    }
  }

  private void populateMap(final Map<String, String> parameters, final XMLStreamReader reader) {
    while(XMLStreamReaderUtils.hasNext(reader)) {
      XMLStreamReaderUtils.next(reader);
      if(XMLStreamReaderUtils.isStartOfElement(reader, "note")) {
        final String type = XMLStreamReaderUtils.getAttributeValue(reader, "type");
        if("input".equals(type)) {
          final String label = XMLStreamReaderUtils.getAttributeValue(reader, "label");
          final String value = XMLStreamReaderUtils.getNextText(reader);
          parameters.put(label, value);
        }
      }
    }
  }

}

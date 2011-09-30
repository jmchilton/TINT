package edu.umn.msi.tropix.models.xtandem.xml;

import java.io.InputStream;

public class XMLUtility extends gov.nih.nci.system.client.util.xml.XMLUtility {
  private static final String MARSHALLER_RESOURCE_NAME = "xml-mapping.xml";
  private static final String UNMARSHALLER_RESOURCE_NAME = "unmarshaller-xml-mapping.xml";
  private static final String DTD_RESOURCE_NAME = "mapping.dtd";
  private static XMLUtility singleton = null;

  private XMLUtility(final InputStream marshallerFileStream, final InputStream unmarshallerFileStream, final InputStream dtdFileStreamCopy1,
      final InputStream dtdFileStreamCopy2) {
    super(new ModifiedCaCOREMarshaller(false), new ModifiedCaCOREUnmarshaller(false));
  }

  public static XMLUtility getInstance() {
    final InputStream marshallerFileStream = XMLUtility.class.getResourceAsStream(MARSHALLER_RESOURCE_NAME);
    final InputStream unmarshallerFileStream = XMLUtility.class.getResourceAsStream(UNMARSHALLER_RESOURCE_NAME);
    return new XMLUtility(marshallerFileStream, unmarshallerFileStream, XMLUtility.class.getResourceAsStream(DTD_RESOURCE_NAME),
        XMLUtility.class.getResourceAsStream(DTD_RESOURCE_NAME));
  }

  synchronized public static XMLUtility getSingletonInstance() {
    if(singleton == null) {
      singleton = getInstance();
    }
    return singleton;
  }

  public Object fromXML(final String xml) throws gov.nih.nci.system.client.util.xml.XMLUtilityException {
    return fromXML(new java.io.StringReader(xml));
  }

  public Object fromXMLPath(final String xmlFilePath) throws gov.nih.nci.system.client.util.xml.XMLUtilityException {
    return fromXML(new java.io.File(xmlFilePath));
  }

  public Object fromXML(final java.io.InputStream stream) throws gov.nih.nci.system.client.util.xml.XMLUtilityException {
    return fromXML(new java.io.InputStreamReader(stream));
  }

  public void toXML(final Object beanObject, final java.io.OutputStream stream) throws gov.nih.nci.system.client.util.xml.XMLUtilityException {
    toXML(beanObject, new java.io.OutputStreamWriter(stream));
  }

}

package edu.umn.msi.tropix.models.sequest.xml;

import gov.nih.nci.system.client.util.xml.XMLUtilityException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;

import org.apache.log4j.Logger;
import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

class ModifiedCaCOREUnmarshaller implements gov.nih.nci.system.client.util.xml.Unmarshaller {
  private static final String UNMARSHALLER_RESOURCE_NAME = "unmarshaller-xml-mapping.xml";
  private static final String DTD_RESOURCE_NAME = "mapping.dtd";
  private static Logger log = Logger.getLogger(ModifiedCaCOREUnmarshaller.class.getName());

  private Mapping mapping;

  /**
   * Creates and XMLUtility instance
   */
  public ModifiedCaCOREUnmarshaller(final boolean validation) {
  }

  /**
   * 
   * @return default mapping file being used for xml serialziation/deserialization
   */
  public Mapping getMapping() throws XMLUtilityException {
    /* if no mapping file explicity specified then load the default */
    // log.debug("mapping is null? " + (mapping==null));
    if(mapping == null) {
      // log.debug("mapping is null; will try to load it");
      try {
        final EntityResolver resolver = new EntityResolver() {
          public InputSource resolveEntity(final String publicId, final String systemId) {
            if(publicId.equals("-//EXOLAB/Castor Object Mapping DTD Version 1.0//EN")) {
              // InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("mapping.dtd");
              final InputStream in = this.getClass().getResourceAsStream(DTD_RESOURCE_NAME); // mappingDTDFileStream;
              return new InputSource(in);
            }
            return null;
          }
        };

        // org.xml.sax.InputSource mappIS = new
        // org.xml.sax.InputSource(Thread.currentThread().getContextClassLoader().getResourceAsStream(mappingFileName));
        final InputStream is = this.getClass().getResourceAsStream(UNMARSHALLER_RESOURCE_NAME);
        final org.xml.sax.InputSource mappIS = new org.xml.sax.InputSource(is);
        final Mapping localMapping = new Mapping();
        localMapping.setEntityResolver(resolver);
        localMapping.loadMapping(mappIS);
        return localMapping;
      } catch(final Exception e) {
        System.out.println("Error reading default xml mapping file: " + e.getMessage());
        log.error("Error reading default xml mapping file: ", e);
        throw new XMLUtilityException("Error reading default xml mapping file ", e);
      }
    }
    return mapping;
  }

  public synchronized Object fromXML(final java.io.Reader input) throws XMLUtilityException {
    Object beanObject;

    org.exolab.castor.xml.Unmarshaller unmarshaller = null;
    try {
      // log.debug("Creating unmarshaller");
      unmarshaller = new org.exolab.castor.xml.Unmarshaller(this.getMapping());
      unmarshaller.setIgnoreExtraElements(true);
    } catch(final MappingException e) {
      log.error("XML mapping file is invalid: ", e);
      throw new XMLUtilityException("XML mapping file invalid: ", e);
    } catch(final Exception e) {
      log.error("General Exception caught trying to create unmarshaller: ", e);
      throw new XMLUtilityException("General Exception caught trying to create unmarshaller: ", e);
    }

    try {
      log.debug("About to unmarshal from input ");
      beanObject = unmarshaller.unmarshal(input);
    } catch(final MarshalException e) {
      log.error("Error marshalling input: ", e);
      throw new XMLUtilityException("Error unmarshalling xml input: " + e.getMessage(), e);
    } catch(final ValidationException e) {
      log.error("Error in xml validation when unmarshalling xml input: ", e);
      throw new XMLUtilityException("Error in xml validation when unmarshalling xml input: ", e);
    }
    return beanObject;
  }

  public Object fromXML(final java.io.File xmlFile) throws XMLUtilityException {
    Object beanObject = null;
    try {
      // log.debug("Reading from file: " + xmlFile.getName());
      final FileReader fRead = new FileReader(xmlFile);
      beanObject = fromXML(fRead);
    } catch(final FileNotFoundException e) {
      log.error("XML input file invalid: ", e);
      throw new XMLUtilityException("XML input file invalid: ", e);
    }
    return beanObject;
  }

  public Object getBaseUnmarshaller() {
    return this;
  }
}

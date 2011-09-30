/*
 * Portions of this file Copyright 1999-2005 University of Chicago
 * Portions of this file Copyright 1999-2005 The University of Southern California.
 *
 * This file or a portion of this file is licensed under the
 * terms of the Globus Toolkit Public License, found at
 * http://www.globus.org/toolkit/download/license.html.
 * If you redistribute this file, with or without
 * modifications, you must include this notice in the file.
 */
package edu.umn.msi.tropix.common.xml;

import java.io.StringReader;

import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.axis.AxisEngine;
import org.apache.axis.Constants;
import org.apache.axis.MessageContext;
import org.apache.axis.client.AxisClient;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.Deserializer;
import org.apache.axis.message.EnvelopeHandler;
import org.apache.axis.message.MessageElement;
import org.apache.axis.message.SOAPHandler;
import org.apache.axis.utils.XMLUtils;

import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;

class ObjectDeserializationContext extends DeserializationContext {

  private static final Log LOGGER = LogFactory.getLog(ObjectDeserializationContext.class.getName());

  private Deserializer topDeserializer = null;

  public ObjectDeserializationContext(final MessageElement element) throws Exception {
    this(element, null);
  }

  public ObjectDeserializationContext(final MessageElement element, final Class javaClass) throws Exception {
    super(getContext(), new SOAPHandler());

    init(element.getType(), javaClass);

    String inputString = element.toString();
    LOGGER.debug(inputString);
    inputSource = new InputSource(new StringReader(inputString));
  }

  public ObjectDeserializationContext(final Element element) throws Exception {
    this(element, null);
  }

  public ObjectDeserializationContext(final Element element, final Class javaClass) throws Exception {
    super(getContext(), new SOAPHandler());

    String typeAttr = element.getAttributeNS(Constants.URI_DEFAULT_SCHEMA_XSI, "type");

    QName type = null;

    if(typeAttr != null && typeAttr.length() > 0) {
      type = XMLUtils.getQNameFromString(typeAttr, element);
    }

    init(type, javaClass);

    String inputString = XMLUtils.ElementToString(element);
    LOGGER.debug(inputString);
    inputSource = new InputSource(new StringReader(inputString));
  }

  public ObjectDeserializationContext(final InputSource input, final Class javaClass) throws Exception {
    super(getContext(), new SOAPHandler());
    init(null, javaClass);
    this.inputSource = input;
  }

  private void setDeserializer(final QName type, final Class javaClass) throws Exception {
    if(type == null && javaClass == null) {
      throw new Exception("typeOrClassRequired");
    }

    if(type != null) {
      // Use the xmlType to get the deserializer.
      this.topDeserializer = getDeserializerForType(type);
    } else {
      QName defaultXMLType = getTypeMapping().getTypeQName(javaClass);
      this.topDeserializer = getDeserializer(javaClass, defaultXMLType);
    }

    if(this.topDeserializer == null) {
      this.topDeserializer = getDeserializerForClass(javaClass);
    }
  }

  private void init(final QName type, final Class javaClass) throws Exception {
    msgContext.setEncodingStyle("");
    popElementHandler();

    setDeserializer(type, javaClass);

    if(topDeserializer == null) {
      String arg = (type == null) ? javaClass.getName() : type.toString();
      throw new Exception("noDeserializer");
    }

    pushElementHandler(new EnvelopeHandler((SOAPHandler) this.topDeserializer));
  }

  // overwrites the superclass method
  public void setDocumentLocator(final Locator locator) {
  }

  public Object getValue() {
    return (this.topDeserializer == null) ? null : this.topDeserializer.getValue();
  }

  public MessageElement getMessageElement() {
    if(this.topDeserializer == null || !(this.topDeserializer instanceof SOAPHandler)) {
      return null;
    }
    return ((SOAPHandler) this.topDeserializer).myElement;
  }

  public QName getQName() {
    MessageElement element = getMessageElement();
    return (element == null) ? null : element.getQName();
  }

  /**
   * Gets MessageContext associated with the current thread. If
   * MessageContext is not associated with the current thread a new
   * one is created, initialized with client AxisEngine.
   */
  public static MessageContext getContext() {
    MessageContext ctx = MessageContext.getCurrentContext();
    if(ctx == null) {
      ctx = new MessageContext(getClientEngine());
      ctx.setEncodingStyle("");
      ctx.setProperty(AxisClient.PROP_DOMULTIREFS, Boolean.FALSE);
    }
    return ctx;
  }

  private static AxisEngine axisClientEngine = null;

  /**
   * Get the default Axis client engine.
   */
  public static synchronized AxisEngine getClientEngine() {
    if(axisClientEngine == null) {
      axisClientEngine = new AxisClient();
    }
    return axisClientEngine;
  }

}

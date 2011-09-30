/*******************************************************************************
 * Copyright 2009 Regents of the University of Minnesota. All rights
 * reserved.
 * Copyright 2009 Mayo Foundation for Medical Education and Research.
 * All rights reserved.
 *
 * This program is made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, EITHER EXPRESS OR
 * IMPLIED INCLUDING, WITHOUT LIMITATION, ANY WARRANTIES OR CONDITIONS
 * OF TITLE, NON-INFRINGEMENT, MERCHANTABILITY OR FITNESS FOR A
 * PARTICULAR PURPOSE.  See the License for the specific language
 * governing permissions and limitations under the License.
 *
 * Contributors:
 * Minnesota Supercomputing Institute - initial API and implementation
 ******************************************************************************/

package edu.umn.msi.tropix.common.xml;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;

import javax.annotation.WillClose;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;

import org.apache.axis.AxisEngine;
import org.apache.axis.Constants;
import org.apache.axis.MessageContext;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.message.MessageElement;
import org.apache.axis.server.AxisServer;
import org.apache.axis.utils.XMLUtils;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import edu.umn.msi.tropix.common.io.FileUtils;
import edu.umn.msi.tropix.common.io.FileUtilsFactory;
import edu.umn.msi.tropix.common.io.IOUtils;
import edu.umn.msi.tropix.common.io.IOUtilsFactory;

public class AxisSerializationUtilsImpl implements AxisSerializationUtils {
  private static final IOUtils IO_UTILS = IOUtilsFactory.getInstance();
  private static final FileUtils FILE_UTILS = FileUtilsFactory.getInstance();

  /**
   * Converts a DOM Element object into a Java object. <br>
   * <b>Note:</b> This operation is slow as it converts the DOM Element
   * into a string which then is deserialized into a Java object.
   */
  public static Object toObject(final Element element, final Class javaClass) throws Exception {
    ObjectDeserializationContext deserializer = new ObjectDeserializationContext(element, javaClass);
    deserializer.parse();
    return deserializer.getValue();
  }

  /*
   * Stolen from CaGrid core Utils.java
   */
  private static Object deserializeObject(final Reader xmlReader, final Class clazz) throws Exception {
    org.w3c.dom.Document doc = XMLUtils.newDocument(new InputSource(xmlReader));
    Object obj = toObject(doc.getDocumentElement(), clazz);
    return obj;
  }

  public <T> T deserialize(@WillClose final Reader reader, final Class<T> clazz) {
    try {
      @SuppressWarnings("unchecked")
      final T deserializedObject = (T) deserializeObject(reader, clazz);
      return deserializedObject;
    } catch(final Exception e) {
      throw new XMLException(e);
    } finally {
      IO_UTILS.closeQuietly(reader);
    }
  }

  public static SOAPElement toSOAPElement(final Object obj, final QName name, final boolean nillable) throws Exception {
    if(obj instanceof MessageElement) {
      MessageElement element = (MessageElement) obj;
      if(name == null || name.equals(element.getQName())) {
        return element;
      } else {
        throw new Exception("notImplemented");
      }
    } else if(obj instanceof Element) {
      Element element = (Element) obj;
      if(name == null || (name.getLocalPart().equals(element.getLocalName()) && name.getNamespaceURI().equals(element.getNamespaceURI()))) {
        return new MessageElement((Element) obj);
      } else {
        throw new Exception("notImplemented");
      }
    }

    if(name == null) {
      throw new IllegalArgumentException("nullArgument");
    }

    MessageElement messageElement = new MessageElement();
    messageElement.setQName(name);
    try {
      messageElement.setObjectValue(obj);
    } catch(Exception e) {
      throw new Exception("genericSerializationError", e);
    }
    if(obj == null && nillable) {
      try {
        messageElement.addAttribute(Constants.NS_PREFIX_SCHEMA_XSI, Constants.URI_DEFAULT_SCHEMA_XSI, "nil", "true");
      } catch(Exception e) {
        throw new Exception("genericSerializationError", e);
      }
    }
    return messageElement;
  }

  /*
   * Stolen from CaGrid core Utils
   */
  private static void serializeObject(final Object obj, final QName qname, final Writer writer) throws Exception {
    // derive a message element for the object
    MessageElement element = (MessageElement) toSOAPElement(obj, qname, false);
    // create a message context
    MessageContext messageContext = new MessageContext(new AxisServer());
    messageContext.setEncodingStyle("");
    messageContext.setProperty(AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
    // the following two properties prevent xsd types from appearing in
    // every single element in the serialized XML
    messageContext.setProperty(AxisEngine.PROP_EMIT_ALL_TYPES, Boolean.FALSE);
    messageContext.setProperty(AxisEngine.PROP_SEND_XSI, Boolean.FALSE);

    // create a serialization context to use the new message context
    SerializationContext serializationContext = new SerializationContext(writer, messageContext);
    serializationContext.setPretty(true);

    // output the message element through the serialization context
    element.output(serializationContext);
    writer.write("\n");
    // writer.close();
    writer.flush();
  }

  public <T> void serialize(final Writer writer, final Object object, final QName qName) {
    try {
      serializeObject(object, qName, writer);
    } catch(final Exception e) {
      throw new XMLException(e);
    }
  }

  public <T> T deserialize(final URL url, final Class<T> clazz) {
    try {
      final Reader reader = new InputStreamReader(url.openStream());
      return deserialize(reader, clazz);
    } catch(final IOException e) {
      throw new XMLException(e);
    }
  }

  public <T> T deserialize(final File file, final Class<T> clazz) {
    return deserialize(FILE_UTILS.getFileReader(file), clazz);
  }

}

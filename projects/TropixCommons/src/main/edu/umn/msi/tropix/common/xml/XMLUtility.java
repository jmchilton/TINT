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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.sax.SAXSource;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLFilter;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.google.common.base.Supplier;

import edu.umn.msi.tropix.common.io.FileUtils;
import edu.umn.msi.tropix.common.io.FileUtilsFactory;
import edu.umn.msi.tropix.common.io.IOUtils;
import edu.umn.msi.tropix.common.io.IOUtilsFactory;
import edu.umn.msi.tropix.common.io.InputContext;
import edu.umn.msi.tropix.common.io.OutputContext;
import edu.umn.msi.tropix.common.io.TempFileSuppliers;

/**
 * Utility class for dealing with JAX-B serialization in a high level and checked
 * exception free mannger.
 *  
 * @author John Chilton
 *
 * @param <T> JAX-B type to operate on.
 */
public class XMLUtility<T> {
  private static final Supplier<File> TEMP_FILE_SUPPLIER = TempFileSuppliers.getDefaultTempFileSupplier();
  private static final FileUtils FILE_UTILS = FileUtilsFactory.getInstance();
  private static final IOUtils IO_UTILS = IOUtilsFactory.getInstance();
  private Class<T> targetClass;
  private String targetPackage;
  private XMLFilter filter;
  private static HashMap<String, JAXBContext> contextsMap = new HashMap<String, JAXBContext>();

  private static synchronized void init(final String packageName) {
    if(!XMLUtility.contextsMap.containsKey(packageName)) {
      try {
        final JAXBContext jaxbContext = JAXBContext.newInstance(packageName);
        XMLUtility.contextsMap.put(packageName, jaxbContext);
      } catch(final Exception e) {
        throw new IllegalStateException("JAXB configuration error, failed to create instance for " + packageName, e);
      }
    }
  }

  public Unmarshaller getUnmarshaller() throws JAXBException {
    return XMLUtility.contextsMap.get(this.targetPackage).createUnmarshaller();
  }

  public Marshaller getMarshaller() throws JAXBException {
    return XMLUtility.contextsMap.get(this.targetPackage).createMarshaller();
  }

  public XMLUtility(final Class<T> clazz) {
    this(clazz.getPackage().getName());
    this.targetClass = clazz;
  }

  private XMLUtility(final String packageName) {
    XMLUtility.init(packageName);
    this.targetPackage = packageName;
  }

  public T deserialize(final String filePath) throws XMLException {
    final File file = new File(filePath);
    return this.deserialize(file);
  }

  public T deserialize(final byte[] bytes) {
    final ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
    return this.deserialize(inputStream);
  }

  public T deserialize(final InputContext inputContext) throws XMLException {
    final File tempFile = XMLUtility.TEMP_FILE_SUPPLIER.get();
    try {
      inputContext.get(tempFile);
      return this.deserialize(tempFile);
    } finally {
      XMLUtility.FILE_UTILS.deleteQuietly(tempFile);
    }
  }

  public T deserialize(final File file) throws XMLException {
    final FileReader reader = XMLUtility.FILE_UTILS.getFileReader(file);
    try {
      return this.deserialize(reader);
    } finally {
      XMLUtility.IO_UTILS.closeQuietly(reader);
    }
  }

  public T fromString(final String string) throws XMLException {
    final StringReader reader = new StringReader(string);
    return this.deserialize(reader);
  }

  public T deserialize(final Reader reader) throws XMLException {
    Object unmarshalled;
    try {
      final Unmarshaller unmarshaller = this.getUnmarshaller(); // unmarshallerMap.get(targetPackage);
      if(filter == null) {
        unmarshalled = unmarshaller.unmarshal(reader);
      } else {
        final InputSource is = new InputSource(reader);
        final SAXSource source = new SAXSource(filter, is);
        unmarshalled = unmarshaller.unmarshal(source);
      }
    } catch(final JAXBException e) {
      throw new XMLParseException("Error deserializing object from XML.", e);
    }
    // Either way return the root element.
    if(unmarshalled instanceof JAXBElement) {
      @SuppressWarnings("rawtypes")
      final JAXBElement element = (JAXBElement) unmarshalled;
      unmarshalled = element.getValue();
    }
    return this.targetClass.cast(unmarshalled);
  }

  public T deserialize(final InputStream stream) throws XMLException {
    final InputStreamReader reader = new InputStreamReader(stream);
    return this.deserialize(reader);
  }

  public void serialize(final T object, final OutputStream stream) {
    final OutputStreamWriter writer = new OutputStreamWriter(stream);
    this.serialize(object, writer);
  }

  public void serialize(final T object, final StringBuffer buffer) {
    buffer.append(this.serialize(object));
  }

  public void serialize(final T object, final String path) {
    final File file = new File(path);
    this.serialize(object, file);
  }
  
  protected void setMarshallerProperties(final Marshaller marshaller) throws PropertyException {    
  }

  public void serialize(final T object, final Writer writer) {
    try {
      final Marshaller marshaller = this.getMarshaller();
      setMarshallerProperties(marshaller);
      marshaller.marshal(object, writer);
    } catch(final JAXBException e) {
      throw new IllegalStateException("JAXB configuration error, failed to serialize object " + object.toString() + " of class " + object.getClass().getName(), e);
    }
  }

  public void serialize(final T object, final File file) {
    final FileWriter writer = XMLUtility.FILE_UTILS.getFileWriter(file);
    try {
      this.serialize(object, writer);
    } finally {
      XMLUtility.IO_UTILS.closeQuietly(writer);
    }
  }

  // TODO: Optimize, there may be a way to do this without creating
  // a temp file.
  public void serialize(final T object, final OutputContext context) {
    final File tempFile = XMLUtility.TEMP_FILE_SUPPLIER.get();
    try {
      this.serialize(object, tempFile);
      context.put(tempFile);
    } finally {
      XMLUtility.FILE_UTILS.deleteQuietly(tempFile);
    }
  }

  public String serialize(final T object) {
    final StringWriter writer = new StringWriter();
    this.serialize(object, writer);
    return writer.toString();
  }

  public String toString(final T object) {
    return this.serialize(object);
  }

  public void setNamespaceFilter(final String namespace) {
    final XMLReader reader;
    try {
      reader = XMLReaderFactory.createXMLReader();
    } catch(SAXException e) {
      throw new RuntimeException(e);
    }
    final NamespaceFilter inFilter = new NamespaceFilter(namespace, true);
    inFilter.setParent(reader);
    setFilter(inFilter);
  }
  
  public void setFilter(final XMLFilter filter) {
    this.filter = filter;
  }
  
}
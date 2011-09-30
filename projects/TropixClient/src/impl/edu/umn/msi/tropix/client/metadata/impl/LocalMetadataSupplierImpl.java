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

package edu.umn.msi.tropix.client.metadata.impl;

import java.io.Reader;
import java.net.URL;

import javax.annotation.WillClose;
import javax.xml.namespace.QName;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

import edu.umn.msi.tropix.common.io.IOUtilsFactory;
import edu.umn.msi.tropix.grid.xml.SerializationUtils;
import edu.umn.msi.tropix.grid.xml.SerializationUtilsFactory;

public class LocalMetadataSupplierImpl<T> implements LocalMetadataSupplier<T> {
  private static final SerializationUtils SERIALIZATION_UTILS = SerializationUtilsFactory.getInstance();
  private final Class<T> metadataClass;
  private QName qName;
  private Supplier<T> objectSupplier;

  public LocalMetadataSupplierImpl(final Class<T> metadataClass) {
    this.metadataClass = metadataClass;
  }

  // n is lowercase for spring
  public void setQname(final QName qName) {
    this.qName = qName;
  }

  // n is lowercase for spring
  public void setQname(final String qNameStr) {
    setQname(QName.valueOf(qNameStr));
  }

  public void setObjectSupplier(final Supplier<T> objectSupplier) {
    this.objectSupplier = objectSupplier;
  }

  public void setMetadataBean(final edu.umn.msi.tropix.grid.metadata.service.MetadataBeanImpl<T> objectSupplier) {
    this.setObjectSupplier(new Supplier<T>() {
      public T get() {
        return objectSupplier.get();
      }
    });
  }

  public void setObject(final T object) {
    setObjectSupplier(Suppliers.ofInstance(object));
  }

  public void setObjectUrl(final URL url) {
    setObject(SERIALIZATION_UTILS.deserialize(url, metadataClass));
  }

  public void setObjectReader(@WillClose final Reader objectReader) {
    try {
      setObject(SERIALIZATION_UTILS.deserialize(objectReader, metadataClass));
    } finally {
      IOUtilsFactory.getInstance().closeQuietly(objectReader);
    }
  }

  public boolean matches(final String address, final QName qName, final Class<?> metadataClass) {
    return this.qName.equals(qName) && this.metadataClass.equals(metadataClass);
  }

  public T get() {
    return objectSupplier.get();
  }

}
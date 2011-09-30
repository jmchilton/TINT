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

package edu.umn.msi.tropix.grid.xml;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;

import javax.annotation.WillClose;
import javax.xml.namespace.QName;

import edu.umn.msi.tropix.common.io.FileUtils;
import edu.umn.msi.tropix.common.io.FileUtilsFactory;
import edu.umn.msi.tropix.common.io.IOUtils;
import edu.umn.msi.tropix.common.io.IOUtilsFactory;
import edu.umn.msi.tropix.common.xml.XMLException;
import gov.nih.nci.cagrid.common.Utils;

public class SerializationUtilsImpl implements SerializationUtils {
  private static final IOUtils IO_UTILS = IOUtilsFactory.getInstance();
  private static final FileUtils FILE_UTILS = FileUtilsFactory.getInstance();

  public <T> T deserialize(@WillClose final Reader reader, final Class<T> clazz) {
    try {
      @SuppressWarnings("unchecked")
      final T deserializedObject = (T) Utils.deserializeObject(reader, clazz);
      return deserializedObject;
    } catch(final Exception e) {
      throw new XMLException(e);
    } finally {
      IO_UTILS.closeQuietly(reader);
    }
  }

  public <T> void serialize(final Writer writer, final Object object, final QName qName) {
    try {
      Utils.serializeObject(object, qName, writer);
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

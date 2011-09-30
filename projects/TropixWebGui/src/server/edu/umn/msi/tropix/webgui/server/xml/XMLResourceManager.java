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

package edu.umn.msi.tropix.webgui.server.xml;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.json.JSONException;
import org.json.XML;

import com.google.common.base.Function;

import edu.umn.msi.tropix.common.io.IOUtils;
import edu.umn.msi.tropix.common.io.IOUtilsFactory;
import edu.umn.msi.tropix.webgui.server.resource.ResourceAccessor;

public class XMLResourceManager {
  private static final IOUtils IO_UTILS = IOUtilsFactory.getInstance();
  private Function<String, ResourceAccessor> resourceAccessorFunction;

  public String getResourceXml(final String identifier) {
    final ResourceAccessor resourceAccessor = this.resourceAccessorFunction.apply(identifier);
    final InputStream stream = resourceAccessor.get();
    return IO_UTILS.toString(stream);
  }

  public String getResourceJSON(final String identifier) {
    final String xml = this.getResourceXml(identifier);
    try {
      return XML.toJSONObject(xml).toString();
    } catch(final JSONException e) {
      throw new IllegalStateException("Failed to convert xml string to json.", e);
    }
  }

  public InputStream getResourceXmlStream(final String identifier) {
    final ResourceAccessor resourceAccessor = this.resourceAccessorFunction.apply(identifier);
    final InputStream stream = resourceAccessor.get();
    return stream;
  }

  public InputStream getResourceJSONStream(final String identifier) {
    //final String xml = this.getResourceXml(identifier);
    return new ByteArrayInputStream(getResourceJSON(identifier).getBytes());
  }

  public void setResourceAccessorFunction(final Function<String, ResourceAccessor> resourceAccessorFunction) {
    this.resourceAccessorFunction = resourceAccessorFunction;
  }

}

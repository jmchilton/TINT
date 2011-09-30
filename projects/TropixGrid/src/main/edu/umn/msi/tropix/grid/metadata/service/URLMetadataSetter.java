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

package edu.umn.msi.tropix.grid.metadata.service;

import java.net.URL;

import edu.umn.msi.tropix.grid.xml.SerializationUtils;
import edu.umn.msi.tropix.grid.xml.SerializationUtilsFactory;

public class URLMetadataSetter<T> extends MetadataSetter<T> {
  private static final SerializationUtils SERIALIZATION_UTILS = SerializationUtilsFactory.getInstance();
  private final URL metadataUrl;
  private final Class<T> metadataClass;

  public URLMetadataSetter(final MetadataBeanImpl<T> metadataBean, final Class<T> metadataClass, final URL metadataUrl) {
    super(metadataBean);
    this.metadataClass = metadataClass;
    this.metadataUrl = metadataUrl;
    update();
  }

  protected T getMetadata() {
    return SERIALIZATION_UTILS.deserialize(metadataUrl, metadataClass);
  }

}

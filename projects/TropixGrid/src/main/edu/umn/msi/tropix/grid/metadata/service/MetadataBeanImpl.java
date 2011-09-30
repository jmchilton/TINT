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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MetadataBeanImpl<T> {
  private static final Log LOG = LogFactory.getLog(MetadataBeanImpl.class);
  private T metadata;

  public void set(final T serviceMetadata) {
    LOG.debug("Setting metadata in MetadataBeanImpl, in a production environment this shouldn't happen probably. Spring should dynamically replace this method with one that updated resource properties.");
    this.metadata = serviceMetadata;
  }

  public T get() {
    return metadata;
  }
}

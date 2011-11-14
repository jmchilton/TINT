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

package edu.umn.msi.tropix.storage.core.access;

import java.io.InputStream;
import java.io.OutputStream;

import edu.umn.msi.tropix.common.io.HasStreamInputContext;

public interface AccessProvider {
  HasStreamInputContext getFile(String id);

  /**
   * Stores stream to file uniquely referenced by
   * id.
   * 
   * @param id
   *          Unique identifier for file.
   * @param inputStream
   *          Contents to save to file.
   * @return The number of bytes in resulting file.
   */
  long putFile(String id, InputStream inputStream);

  OutputStream getPutFileOutputStream(String id);

  boolean deleteFile(String id);

  boolean fileExists(String id);

  long getLength(String id);

  long getDateModified(String id);
}

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

package edu.umn.msi.tropix.labs.requests.impl;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.umn.msi.tropix.common.io.FileUtils;
import edu.umn.msi.tropix.common.io.FileUtilsFactory;
import edu.umn.msi.tropix.common.logging.ExceptionUtils;

public class RequestDirectoryCreatorImpl implements RequestDirectoryCreator {
  private static final Log LOG = LogFactory.getLog(RequestDirectoryCreatorImpl.class);
  private static final FileUtils FILE_UTILS = FileUtilsFactory.getInstance();
  private File baseDirectory;

  public void createDirectory(final String serviceId, final String requestId) {
    File newDirectory = null;
    try {
      newDirectory = new File(new File(baseDirectory, serviceId), requestId);
      if(!FILE_UTILS.mkdirs(newDirectory)) {
        throw new IllegalStateException("Failed to create directory");
      }
    } catch(final RuntimeException e) {
      ExceptionUtils.logQuietly(LOG, e, "Failed to create directory for request " + newDirectory + ". Manual intervention is needed.");
    }
  }

  public void setBaseDirectory(final File baseDirectory) {
    this.baseDirectory = baseDirectory;
  }
}

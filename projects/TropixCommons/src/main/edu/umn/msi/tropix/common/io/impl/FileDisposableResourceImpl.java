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

package edu.umn.msi.tropix.common.io.impl;

import java.io.File;

import javax.annotation.concurrent.Immutable;

import edu.umn.msi.tropix.common.io.DisposableResource;
import edu.umn.msi.tropix.common.io.FileUtils;
import edu.umn.msi.tropix.common.io.FileUtilsFactory;

/**
 * This class is marked as final so that subclasses may not prevent it from reclaiming resources upon no longer being strongly reachable.
 * 
 * This class is not thread safe.
 * 
 * @author John Chilton (chilton at msi dot umn dot edu)
 * 
 */
@Immutable
public final class FileDisposableResourceImpl implements DisposableResource {
  private static final FileUtils FILE_UTILS = FileUtilsFactory.getInstance();
  private File file = null;

  public FileDisposableResourceImpl(final File file) {
    this.file = file;
  }

  public void dispose() {
    FILE_UTILS.deleteQuietly(this.file);
  }

  public File getFile() {
    return this.file;
  }

  protected void finalize() {
    this.dispose();
  }
}

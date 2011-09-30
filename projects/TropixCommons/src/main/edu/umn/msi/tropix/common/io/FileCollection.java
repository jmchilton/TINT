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

package edu.umn.msi.tropix.common.io;

import java.io.File;

import org.apache.commons.io.FileUtils;

/**
 * Class meant to describe a collection of files. This class should probably not be used.
 * 
 * @author John Chilton
 *
 */
@Deprecated
public class FileCollection {
  private File[] files;

  public FileCollection() {
    this(new File[0]);
  }

  public FileCollection(final File[] files) {
    this.init(files);
  }

  private void init(final File[] files) {
    this.files = files;
  }

  public boolean deleteAll() {
    boolean allDeleted = true;
    final File[] files = this.getFilesArray();
    for(final File file : files) {
      allDeleted = FileUtils.deleteQuietly(file) && allDeleted;
    }
    return allDeleted;
  }

  public File[] getFilesArray() {
    return this.files;
  }

  public void setFiles(final File[] files) {
    this.files = files;
  }
}

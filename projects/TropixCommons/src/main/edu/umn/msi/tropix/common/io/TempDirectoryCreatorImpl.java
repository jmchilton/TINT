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
import java.util.concurrent.atomic.AtomicLong;

/**
 * A default implementation of {@link TempDirectoryCreator}. This class can be
 * configured to create directories in ${java.io.tmpdir} or in a specified directory.
 * 
 * @author John Chilton
 *
 */
public class TempDirectoryCreatorImpl implements TempDirectoryCreator {
  private static final File DEFAULT_TEMP_DIRECTORY = new File(System.getProperty("java.io.tmpdir"));
  private final AtomicLong counter = new AtomicLong(0L);
  private File tempDirectoryBase;

  public String toString() {
    return "TempDirectoryCreateImpl[baseDir=" + this.tempDirectoryBase + "]";
  }

  public TempDirectoryCreatorImpl(final Object object) {
    if(object instanceof String) {
      this.init(new File((String) object));
    } else if(object instanceof File) {
      this.init((File) object);
    } else {
      this.init(new File(TempDirectoryCreatorImpl.DEFAULT_TEMP_DIRECTORY, "tempDirectoryCreator" + File.separator + object.getClass().getName()));
    }
  }

  private void init(final File tempDirectoryBase) {
    this.tempDirectoryBase = tempDirectoryBase;
    if(!this.tempDirectoryBase.exists()) {
      if(!this.tempDirectoryBase.mkdirs()) {
        throw new IllegalStateException("Failed to create specified temproary directory base.");
      }
    }
  }

  public File getNewTempDirectory() {
    while(true) {
      final long thisCount = this.counter.getAndIncrement();
      final File newDir = new File(this.tempDirectoryBase, "" + thisCount);
      if(newDir.exists()) {
        continue;
      } else {
        if(!newDir.mkdirs()) {
          throw new IllegalStateException("Cannot seem to create new temp directory.");
        } else {
          return newDir;
        }
      }
    }
  }
}

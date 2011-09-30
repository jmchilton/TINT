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

package edu.umn.msi.tropix.webgui.server.storage.impl;

import java.io.File;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.ManagedBean;
import javax.annotation.Nullable;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.collect.MapMaker;

import edu.umn.msi.tropix.common.concurrent.Timer;
import edu.umn.msi.tropix.common.io.FileUtils;
import edu.umn.msi.tropix.common.io.FileUtilsFactory;
import edu.umn.msi.tropix.webgui.server.storage.TempFileStore;

@ManagedBean
class TempFileStoreImpl implements TempFileStore {
  private static final Log LOG = LogFactory.getLog(TempFileStoreImpl.class);
  private static final FileUtils FILE_UTILS = FileUtilsFactory.getInstance();
  private ConcurrentMap<String, TempFileInfo> files = new MapMaker().makeMap();
  private Timer timer;

  @Inject
  TempFileStoreImpl(final Timer timer) {
    LOG.info("Instantiating TempFileStoreImpl");
    this.timer = timer;
  }
  
  public static class TempFileInfoImpl implements TempFileInfo {
    private final String fileName;
    private final File tempLocation = FILE_UTILS.createTempFile();
    private final String uuid = UUID.randomUUID().toString();

    public TempFileInfoImpl(final String fileName) {
      this.fileName = fileName;
      tempLocation.deleteOnExit();
    }

    public String getFileName() {
      return fileName;
    }

    public File getTempLocation() {
      return tempLocation;
    }

    public String getId() {
      return uuid;
    }

    @Override
    public String toString() {
      return "TempFileInfoImpl [fileName=" + fileName + ", tempLocation=" + tempLocation + ", uuid=" + uuid + "]";
    }

  }

  public TempFileInfo getTempFileInfo(final String fileName) {
    final TempFileInfo info = new TempFileInfoImpl(fileName);
    timer.schedule(new Runnable() {
      public void run() {
        LOG.info("Cleaning up temp file " + info);
        files.remove(info.getId());
        FILE_UTILS.deleteQuietly(info.getTempLocation());
      }
    }, 30 * 60 * 1000);
    files.put(info.getId(), info);
    LOG.info("Created TempFileInfo object " + info);
    return info;
  }


  @Nullable
  public TempFileInfo recoverTempFileInfo(final String id) {
    return files.get(id);
  }

}

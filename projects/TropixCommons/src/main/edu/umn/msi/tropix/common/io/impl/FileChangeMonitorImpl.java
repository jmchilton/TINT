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
import java.util.List;
import java.util.concurrent.Executor;

import com.google.common.collect.Lists;

import edu.umn.msi.tropix.common.collect.Closure;
import edu.umn.msi.tropix.common.io.FileChangeMonitor;

public class FileChangeMonitorImpl implements FileChangeMonitor {
  private Executor loopingExecutor;
  private final List<MonitorState> files = Lists.newArrayList();
  private boolean started = false;

  class MonitorRunnableImpl implements Runnable {
    public void run() {
      for(final MonitorState file : files) {
        file.checkUpdated();
      }
    }
  }

  public void registerChangeListener(final File file, final Closure<File> listener) {
    synchronized(files) {
      files.add(new MonitorState(file, listener));
      if(!started) {
        loopingExecutor.execute(new MonitorRunnableImpl());
        started = true;
      }
    }
  }

  public void setLoopingExecutor(final Executor loopingExecutor) {
    this.loopingExecutor = loopingExecutor;
  }

  private static class MonitorState {
    private final File file;
    private long lastModified;
    private final Closure<File> listener;

    MonitorState(final File file, final Closure<File> listener) {
      this.file = file;
      this.listener = listener;
      this.lastModified = file.lastModified();
    }

    void checkUpdated() {
      final long lastModified = this.lastModified;
      this.lastModified = file.lastModified();
      if(lastModified != this.lastModified) {
        listener.apply(file);
      }
    }
  }

}

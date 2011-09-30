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
import java.util.concurrent.Executor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.base.Supplier;

import edu.umn.msi.tropix.common.collect.Closure;
import edu.umn.msi.tropix.common.collect.Closures;
import edu.umn.msi.tropix.common.concurrent.LoopingRunnable;
import edu.umn.msi.tropix.common.io.DirectoryMonitor;
import edu.umn.msi.tropix.common.io.FileUtils;
import edu.umn.msi.tropix.common.io.FileUtilsFactory;

class DirectoryMonitorImpl implements DirectoryMonitor {
  private static final Log LOG = LogFactory.getLog(DirectoryMonitorImpl.class);
  private static final FileUtils FILE_UTILS = FileUtilsFactory.getInstance();

  private Supplier<LoopingRunnable> loopingRunnableSupplier;
  private Executor executor;
  private Closure<File> fileClosure;

  public void setFileClosure(final Closure<File> fileClosure) {
    this.fileClosure = fileClosure;
  }

  public void setLoopingRunnableSupplier(final Supplier<LoopingRunnable> loopingRunnableSupplier) {
    this.loopingRunnableSupplier = loopingRunnableSupplier;
  }

  public void setExecutor(final Executor executor) {
    this.executor = executor;
  }

  public void monitor(final File directory) {
    final Runnable pollingRunnable = new Runnable() {
      public void run() {
        LOG.trace("About to iterate over files in results directory");
        Closures.forEach(FILE_UTILS.iterateFiles(directory, null, true), fileClosure);
        LOG.trace("Done iterating over files in results directory");
      }
    };
    final LoopingRunnable loopingRunnable = loopingRunnableSupplier.get();
    loopingRunnable.setBaseRunnable(pollingRunnable);
    executor.execute(loopingRunnable);    
  }

}

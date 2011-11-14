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

package edu.umn.msi.tropix.storage.core.monitor;

import java.io.File;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.umn.msi.tropix.common.io.DirectoryMonitor;

class StorageDirectoryMonitor {
  private static final Log LOG = LogFactory.getLog(StorageDirectoryMonitor.class);
  private final DirectoryMonitor directoryMonitor;
  private final MonitorConfig monitorConfig;

  StorageDirectoryMonitor(final DirectoryMonitor directoryMonitor,
                          final MonitorConfig monitorConfig) {
    this.directoryMonitor = directoryMonitor;
    this.monitorConfig = monitorConfig;
  }
  
  @PostConstruct
  public void init() {
    for(final File directory : monitorConfig.getDirectories()) {
      LOG.info("Starting storage directory monitor for directory " + directory);
      directoryMonitor.monitor(directory);
      LOG.info("Started storage directory monitor for directory " + directory);
    }
  }
  
}

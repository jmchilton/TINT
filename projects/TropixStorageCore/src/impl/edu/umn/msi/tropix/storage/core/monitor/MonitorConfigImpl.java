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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;

import com.google.common.collect.ImmutableMap;

import edu.umn.msi.tropix.common.xml.XMLUtility;
import edu.umn.msi.tropix.storage.core.monitor.config.Directory;
import edu.umn.msi.tropix.storage.core.monitor.config.MonitorDirectories;

class MonitorConfigImpl implements MonitorConfig {
  private static final Log LOG = LogFactory.getLog(MonitorConfigImpl.class);
  private static final XMLUtility<MonitorDirectories> XML_UTILITY = new XMLUtility<MonitorDirectories>(MonitorDirectories.class);
  private MonitorDirectories monitorDirectories;
  private ImmutableMap<File, String> directoryMap;
  
  public void setConfigFile(final String path) {
    LOG.info("In setConfigFile with path " + path);
    ImmutableMap.Builder<File, String> directoryMapBuilder = ImmutableMap.builder();
    if(StringUtils.hasText(path) && !path.startsWith("$")) {
      final File configFile = new File(path);
      if(configFile.exists()) {
        this.monitorDirectories = XML_UTILITY.deserialize(configFile);
        for(Directory directory : monitorDirectories.getDirectory()) {
          directoryMapBuilder.put(new File(directory.getPath()), directory.getSharedFolderName());
        }              
      } else {
        LOG.warn("Specified monitor config file path " + path + " but no such file exists.");
      }
    }
    this.directoryMap = directoryMapBuilder.build();
  }
  
  public Iterable<File> getDirectories() {
    return directoryMap.keySet();
  }
  
  public String getSharedFolderName(final File directory) {
    return directoryMap.get(directory);
  }
    
}

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

package edu.umn.msi.tropix.common.spring;

import java.io.File;

import org.springframework.util.StringUtils;

import com.google.common.base.Supplier;

public class TropixConfigDirPropertyPlaceholderConfigurer extends SinglePropertyPlaceholderConfigurer implements Supplier<String> {

  // <HACK> This one hack is all I need to radically reconfigure Tropix for integration testing and ignore
  // all user's existing configuration. I have tried to come up with another way, but I am not seeing it.
  private static final ThreadLocal<String> CONFIG_DIR_OVERRIDE = new ThreadLocal<String>();
  
  public static final void overrideConfigDir(final File configDir) {
    CONFIG_DIR_OVERRIDE.set(configDir.getAbsolutePath());
  }

  public static String getConfigDir() {
    String configDir = CONFIG_DIR_OVERRIDE.get();
    if(configDir == null) {
      if(!StringUtils.hasText(System.getProperty("tropix.config.dir"))) {
        configDir = System.getProperty("user.home") + File.separator + ".tropix";
      } else {
        configDir = System.getProperty("tropix.config.dir");
      }
    }
    return configDir;
  }
  
  public TropixConfigDirPropertyPlaceholderConfigurer() {
    super("tropix.config.dir", getConfigDir());
  }

}

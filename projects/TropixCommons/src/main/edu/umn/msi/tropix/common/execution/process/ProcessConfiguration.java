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

package edu.umn.msi.tropix.common.execution.process;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * A configuration bean for describing system processes to
 * launch with a {@link ProcessFactory} object.
 * 
 * @author John Chilton
 *
 */
public class ProcessConfiguration {
  private Map<String, String> environment = null;
  private String application = null;
  private List<String> arguments = null;
  private File directory = null;

  public Map<String, String> getEnvironment() {
    return this.environment;
  }

  public void setEnvironment(final Map<String, String> environment) {
    this.environment = environment;
  }

  public String getApplication() {
    return this.application;
  }

  public void setApplication(final String application) {
    this.application = application;
  }

  public List<String> getArguments() {
    return this.arguments;
  }

  public void setArguments(final List<String> arguments) {
    this.arguments = arguments;
  }

  public File getDirectory() {
    return this.directory;
  }

  public void setDirectory(final File directory) {
    this.directory = directory;
  }

}

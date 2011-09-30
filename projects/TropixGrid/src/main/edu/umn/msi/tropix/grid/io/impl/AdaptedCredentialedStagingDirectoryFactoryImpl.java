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

package edu.umn.msi.tropix.grid.io.impl;

import edu.umn.msi.tropix.common.io.StagingDirectory;
import edu.umn.msi.tropix.common.io.StagingDirectoryFactory;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.grid.io.CredentialedStagingDirectoryFactory;

/**
 * This class wraps a normal staging directory factory and adapts it the CredentialedStagingDirectoryFactory interface by merely ignoring the supplier proxy.
 * 
 * @author John Chilton
 * 
 */
public class AdaptedCredentialedStagingDirectoryFactoryImpl implements CredentialedStagingDirectoryFactory {
  private StagingDirectoryFactory stagingDirectoryFactory;

  public StagingDirectory get(final Credential proxy) {
    return stagingDirectoryFactory.get();
  }

  public StagingDirectory get(final Credential proxy, final String stagingDirectoryPath) {
    return stagingDirectoryFactory.get(stagingDirectoryPath);
  }

  public void setStagingDirectoryFactory(final StagingDirectoryFactory stagingDirectoryFactory) {
    this.stagingDirectoryFactory = stagingDirectoryFactory;
  }

}

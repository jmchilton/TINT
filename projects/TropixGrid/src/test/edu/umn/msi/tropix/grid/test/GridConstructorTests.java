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

package edu.umn.msi.tropix.grid.test;

import edu.umn.msi.tropix.common.test.ConstructorTest;
import edu.umn.msi.tropix.grid.EprUtils;
import edu.umn.msi.tropix.grid.GridServiceFactories;
import edu.umn.msi.tropix.grid.credentials.Credentials;
import edu.umn.msi.tropix.grid.credentials.GlobusCredentialFactories;
import edu.umn.msi.tropix.grid.gridftp.GridFtpClientUtils;
import edu.umn.msi.tropix.grid.xml.SerializationUtilsFactory;

public class GridConstructorTests extends ConstructorTest {
  {
    getClasses().add(GridFtpClientUtils.class);
    getClasses().add(Credentials.class);
    getClasses().add(SerializationUtilsFactory.class);
    getClasses().add(EprUtils.class);
    getClasses().add(GridServiceFactories.class);
    getClasses().add(GlobusCredentialFactories.class);
  }
  
}

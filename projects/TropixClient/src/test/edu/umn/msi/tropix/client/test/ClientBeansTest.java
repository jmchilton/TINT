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

package edu.umn.msi.tropix.client.test;

import org.testng.annotations.Test;

import edu.umn.msi.tropix.client.credential.GlobusCredentialOptions;
import edu.umn.msi.tropix.client.directory.GridUser;
import edu.umn.msi.tropix.client.request.RequestBean;
import edu.umn.msi.tropix.client.search.models.GridData;
import edu.umn.msi.tropix.client.search.models.GridFile;
import edu.umn.msi.tropix.client.services.GridService;
import edu.umn.msi.tropix.client.services.IdentificationGridService;
import edu.umn.msi.tropix.client.services.QueueGridService;
import edu.umn.msi.tropix.common.test.BeanTest;

public class ClientBeansTest extends BeanTest {
  {
    this.getTestObjects().add(new RequestBean());
    this.getTestObjects().add(new GridService());
    this.getTestObjects().add(new QueueGridService());
    this.getTestObjects().add(new IdentificationGridService());
    this.getTestObjects().add(new GridUser());
    this.getTestObjects().add(new GridFile());
    this.getTestObjects().add(new GridData());
    this.getTestObjects().add(new GlobusCredentialOptions());
  }

  @Test(groups = "unit")
  public void beans() {
    super.beanTest();
  }
}

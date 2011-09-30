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

package edu.umn.msi.tropix.persistence.dao.hibernate.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.persistence.dao.TropixObjectDao;
import edu.umn.msi.tropix.persistence.service.security.SecurityProvider;

public class HqlJoinTest extends DaoTest {
  @Autowired
  private TropixObjectDao tropixObjectDao;

  @Autowired
  private SecurityProvider securityProvider;

  @Test
  public void canRead() {
    final String permissionId = newId(), userId = newId(), objectId = newId();
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO USER(ID,CAGRID_ID) VALUES ('" + userId + "','" + userId + "')");
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO PERMISSION(ID,ROLE) VALUES ('" + permissionId + "','owner')");
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO DIRECT_PERMISSION(PERMISSION_ID) VALUES ('" + permissionId + "')");
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO TROPIX_OBJECT(ID) VALUES ('" + objectId + "')");
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO JOIN_PERMISSION_USER(PERMISSION_ID,USER_ID) VALUES ('" + permissionId + "','" + userId + "')");
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO JOIN_PERMISSION_OBJECT(PERMISSION_ID,OBJECT_ID) VALUES ('" + permissionId + "','" + objectId + "')");

    final long start = System.currentTimeMillis();
    assert securityProvider.canModify(objectId, userId);
    final long end = System.currentTimeMillis();
    System.out.println(end - start + " milliseconds");
  }

}

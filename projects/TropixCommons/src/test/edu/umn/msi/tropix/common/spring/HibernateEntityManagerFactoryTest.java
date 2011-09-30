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

import java.net.MalformedURLException;

import org.testng.annotations.Test;

public class HibernateEntityManagerFactoryTest {

  /**
   * This is the kind of class that requires integration testing, but there 
   * are some simple things that can be checked.
   * @throws MalformedURLException 
   */
  @Test(groups = "unit")
  public void testPropertiesSet() throws MalformedURLException {
    HibernateEntityManagerFactory factory = new HibernateEntityManagerFactory();
    factory.setDialect("org.hibernate.sql");
    factory.setPersistenceUnitName("moo");
    factory.getPersistenceUnitName().equals("moo");
    factory.setAutoDdl("update");
    factory.setShowSql(true);
    
    // Verify easy properties set appropriately
    assert factory.getJpaPropertyMap().get("show_sql").equals(true);
    assert factory.getJpaPropertyMap().get("hibernate.hbm2ddl.auto").equals("update");

  }
}

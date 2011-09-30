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

import java.util.Map;

import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

public class HibernateEntityManagerFactory extends LocalContainerEntityManagerFactoryBean {
  private boolean showSql = false;

  public void setDialect(final String dialect) {
    final HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
    adapter.setDatabasePlatform(dialect);
    adapter.setShowSql(showSql);
    setJpaVendorAdapter(adapter);
  }

  private Map<String, Object> getPropertyMap() {
    final Map<String, Object> propertyMap = getJpaPropertyMap();
    return propertyMap;
  }

  public void setAutoDdl(final String autoDdl) {
    getPropertyMap().put("hibernate.hbm2ddl.auto", autoDdl);
  }

  public void setShowSql(final boolean showSql) {
    this.showSql = showSql;
    getPropertyMap().put("show_sql", showSql);
  }

  @Override
  public void setPersistenceUnitName(final String persistenceUnitName) {
    super.setPersistenceUnitName(persistenceUnitName);
    super.setPersistenceXmlLocation("classpath:META-INF/" + persistenceUnitName + "/persistence.xml");
  }

}

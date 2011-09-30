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

package edu.umn.msi.tropix.persistence.dao.hibernate;

import java.io.Serializable;
import java.util.Collection;
import java.util.UUID;

import org.springframework.orm.hibernate3.HibernateTemplate;

import edu.umn.msi.tropix.common.reflect.ReflectionHelper;
import edu.umn.msi.tropix.common.reflect.ReflectionHelpers;

/**
 * Extends HibernateTemplate to implement to automatically provide grid ids to objects as they are saved.
 * 
 * @author John Chilton (chilton at msi dot umn dot edu)
 * 
 */
public class TropixPersistenceTemplate extends HibernateTemplate {
  private static final ReflectionHelper REFLECTION_HELPER = ReflectionHelpers.getInstance();

  private void attemptToSetId(final Object object) {
    try {
      final String currentId = (String) REFLECTION_HELPER.invoke("getId", object);
      if(currentId == null) {
        REFLECTION_HELPER.invoke("setId", object, UUID.randomUUID().toString());
      }
    } catch(final RuntimeException e) {
      return;
    }
  }

  @Override
  public Serializable save(final Object object) {
    attemptToSetId(object);
    return super.save(object);
  }

  @Override
  public void saveOrUpdate(final Object object) {
    attemptToSetId(object);
    super.saveOrUpdate(object);
  }

  @Override
  @SuppressWarnings("unchecked")
  public void saveOrUpdateAll(final Collection entities) {
    for(final Object object : entities) {
      attemptToSetId(object);
    }
    super.saveOrUpdateAll(entities);
  }

}

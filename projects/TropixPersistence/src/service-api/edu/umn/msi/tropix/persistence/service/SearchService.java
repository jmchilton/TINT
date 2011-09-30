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

package edu.umn.msi.tropix.persistence.service;

import java.util.Collection;

import javax.annotation.Nullable;

import edu.umn.msi.tropix.models.TropixObject;
import edu.umn.msi.tropix.models.utils.TropixObjectType;
import edu.umn.msi.tropix.persistence.aop.AutoUser;
import edu.umn.msi.tropix.persistence.aop.PersistenceMethod;
import edu.umn.msi.tropix.persistence.aop.Reads;
import edu.umn.msi.tropix.persistence.aop.UserId;

public interface SearchService {
  @PersistenceMethod Collection<TropixObject> getChildren(@UserId @AutoUser String userId, @Reads String objectId);

  @PersistenceMethod Collection<TropixObject> getTopLevelObjects(@UserId @AutoUser String userId, @AutoUser String ownerId);

  @PersistenceMethod TropixObject[] searchObjects(@UserId String userId, @Nullable String name, @Nullable String description, @AutoUser @Nullable String ownerId, @Nullable TropixObjectType objectType);

  @PersistenceMethod TropixObject[] quickSearchObjects(@UserId String userId, @Nullable String query);

}

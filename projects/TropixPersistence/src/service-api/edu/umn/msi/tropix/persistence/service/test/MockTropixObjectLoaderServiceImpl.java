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

package edu.umn.msi.tropix.persistence.service.test;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import edu.umn.msi.tropix.common.collect.Closure;
import edu.umn.msi.tropix.common.collect.Closures;
import edu.umn.msi.tropix.models.TropixObject;
import edu.umn.msi.tropix.models.utils.TropixObjectType;
import edu.umn.msi.tropix.persistence.service.TropixObjectLoaderService;

public class MockTropixObjectLoaderServiceImpl implements TropixObjectLoaderService {
  private final Map<String, TropixObject> objects = Maps.newHashMap();
  private Closure<TropixObject> saveListener = Closures.nullClosure();
  
  public void registerSaveListener(final Closure<TropixObject> saveListener) {
    this.saveListener = saveListener;
  }
  
  public void saveObject(final TropixObject object) {
    if(object.getId() == null) {
      object.setId(UUID.randomUUID().toString());
    }
    saveListener.apply(object);
    objects.put(object.getId(), object);
  }
  
  public void saveObjects(final TropixObject... objects) {
    saveObjects(Arrays.asList(objects));
  }
  
  public void saveObjects(final Iterable<? extends TropixObject> objects) {
    for(TropixObject object : objects) {
      saveObject(object);
    }
  }

  public TropixObject load(final String userId, final String objectId) {
    Preconditions.checkNotNull(userId);
    return objects.get(objectId);
  }

  public TropixObject load(final String userId, final String objectId, final TropixObjectType type) {
    Preconditions.checkNotNull(userId);
    final TropixObject object = objects.get(objectId);
    Preconditions.checkNotNull(object, "Attempt to load object with id " + objectId + " have ids " + Joiner.on(",").join(objects.keySet()));
    Preconditions.checkArgument(type.isInstance(object));
    return object;
  }

  public TropixObject[] load(final String userId, final String[] objectIds, final TropixObjectType type) {
    Preconditions.checkNotNull(userId);
    final int length = objectIds.length;
    final TropixObject[] loadedObjects = new TropixObject[length];
    for(int i = 0; i < length; i++) {
      loadedObjects[i] = load(userId, objectIds[i], type);
    }
    return loadedObjects;
  }
  
}

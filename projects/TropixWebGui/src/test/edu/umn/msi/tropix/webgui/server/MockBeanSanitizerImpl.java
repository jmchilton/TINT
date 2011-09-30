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

package edu.umn.msi.tropix.webgui.server;

import java.util.Collection;

import com.google.common.collect.Lists;

import edu.umn.msi.tropix.webgui.server.models.BeanSanitizer;

public class MockBeanSanitizerImpl implements BeanSanitizer {
  private Collection<Object> sanitizedObjects = Lists.newLinkedList();

  public <T> T sanitize(final T object) {
    sanitizedObjects.add(object);
    return object;
  }

  public boolean wasSanitized(final Object object) {
    boolean wasSanitized = false;
    for(final Object sanitizedObject : sanitizedObjects) {
      if(sanitizedObject == object) { // We are not using contains, because this should be a == check, not equals
        wasSanitized = true;
        break;
      }
    }
    return wasSanitized;
  }

}

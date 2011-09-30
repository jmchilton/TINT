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

package edu.umn.msi.tropix.client.request.actions.impl;

import java.util.Collection;

public interface PersistentActionService {
  /**
   * 
   * @param requestAction
   * @return An id for the persisted request action used to later signal the action is complete.
   */
  void persistRequestAction(RequestAction requestAction);

  /**
   * Removes the action specified by actionId from the persistence context so that loadActions() will no longer return it.
   * 
   * @param actionId
   */
  void completeAction(long actionId);

  /**
   * Returns a collection containing all of the actions that have been persisted using the persistRequestAction method, but not completed via the completeAction method.
   * 
   * @return The persisted actions
   */
  Collection<RequestAction> loadActions();

}

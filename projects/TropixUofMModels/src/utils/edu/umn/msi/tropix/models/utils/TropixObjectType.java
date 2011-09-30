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

package edu.umn.msi.tropix.models.utils;

import java.io.Serializable;

import edu.umn.msi.tropix.models.TropixObject;

/**
 * In interface that encapsulated all of the method present on TropixObjectTypeEnum.
 * 
 * @author John Chilton
 * 
 */
public interface TropixObjectType extends Serializable {

  /**
   * 
   * @param object Object to check type of.
   * @return True iff object is and instance of the class represented by the 
   *   described by this object.
   */
  boolean isInstance(TropixObject object);

  /**
   * 
   * @return Fully qualified class name of the type represented by this object.
   */
  String getClassName();

  /**
   * 
   * @return A TropixObjectType description for the super class ot the class
   * described by this type.
   */
  TropixObjectType getParentType();

}

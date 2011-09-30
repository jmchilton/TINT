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

package edu.umn.msi.tropix.common.concurrent;

/**
 * The purpose of this class is to allows objects to wait for the 
 * initialization of other objects base on a key of type {@code K}. 
 * The objects to be initialized can be initialized individually 
 * or all at once. 
 * 
 * This class really shouldn't need to exists and likely indicates some
 * poor design choices in the classes making use of it. It should be 
 * depreciated at some point in the future.
 * 
 * @author John Chilton
 *
 */
public interface InitializationTracker<K> {
  
  public static class InitializationFailedException extends RuntimeException {
    private static final long serialVersionUID = -8160487845269935307L;
  }

  void initialize(K key);

  void fail(K key);

  void waitForInitialization(K key) throws InitializationTracker.InitializationFailedException;

  void initializeAll();

  void waitForAllInitialization();

}
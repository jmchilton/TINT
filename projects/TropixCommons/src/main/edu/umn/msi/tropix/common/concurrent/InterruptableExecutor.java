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
 * Classes that implement this interface are meant to separate the logic from how to deal
 * with {@link InterruptException}s from the underlying logic.
 * 
 * @author John Chilton
 *
 */
public interface InterruptableExecutor {
  /**
   * Can be used to separate the logic of how to repeatedly attempt to execute a piece of code 
   * from the caller of that code.
   * 
   * @param interruptable
   *          The object to execute.
   * @throws edu.umn.msi.tropix.common.concurrent.InterruptedRuntimeException
   *           A wrapper around the last InterruptedException thrown by the input Interruptable object before the executor gives up.
   */
  void execute(Interruptable interruptable) throws edu.umn.msi.tropix.common.concurrent.InterruptedRuntimeException;
}

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

package edu.umn.msi.tropix.common.execution;

import javax.annotation.Nullable;

/**
 * This is the central interface of this package, it is meant as a higher level way
 * to launch and monitor system processes than that provided by java.lang.Process. 
 * 
 * @author John Chilton
 *
 */
public interface ExecutionFactory {
  /**
   * Builds and starts a process according to the ExecutionConfiguration specified.
   * 
   * @param executionConfiguration Bean describing the system process to launch.
   * @param executionCallback Callback executed upon return of the process described by
   *   {@code executionConfiguration}.
   * @return An interface used to monitor and interact with the resulting process.
   */
  ExecutionHook execute(ExecutionConfiguration executionConfiguration, 
                        @Nullable ExecutionCallback executionCallback);
}

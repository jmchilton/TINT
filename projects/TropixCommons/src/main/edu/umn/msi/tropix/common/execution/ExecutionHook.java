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

/**
 * This interface defines objects designed to interact with system processes
 * created with instances of ExecutionFactoryImpl.
 * 
 * @author John Chilton
 *
 */
public interface ExecutionHook {
  /**
   * Kills the associated process.
   */
  void kill();

  /**
   * Waits for the process to complete, and then returns.
   */
  void waitFor();

  /**
   * @return true iff the process completed running
   */
  boolean getComplete();

  /**
   * @return true iff the process was killed, this may have been done manually or internally, for instance in response to the process timing out.
   */
  boolean getKilled();

  /**
   * @return true iff the process timed out
   */
  boolean getTimedOut();

  /**
   * @return The ExecutionConfiguration object send to the ExecutorFactory to create this Hook.
   */
  ExecutionConfiguration getExecutionConfiguration();

  /**
   * @return Return value of executed process
   */
  int getReturnValue();

  /**
   * If an exception occurred while executing this job, this method will return
   * this exception.
   * 
   * @return The exception thrown.
   */
  Exception getException();
}

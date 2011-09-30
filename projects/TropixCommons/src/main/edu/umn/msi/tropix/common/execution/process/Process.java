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

package edu.umn.msi.tropix.common.execution.process;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Interface to wrap Java's abstract java.lang.Process class in. Interfaces have 
 * many advantages over abstract classes in Java, this interface is here to take 
 * advantage of them.
 * 
 * @author John Chilton
 * 
 */
public interface Process {
  void destroy();

  int exitValue();

  InputStream getErrorStream();

  InputStream getInputStream();

  OutputStream getOutputStream();

  int waitFor() throws InterruptedException;

  /**
   * Seems like this should be on the process class, so you can check 
   * for completion without needing to either block (with waitFor) or 
   * catch an exception (with exitValue).
   * 
   * @return Whether the process has completed its execution.
   */
  boolean isComplete();
}

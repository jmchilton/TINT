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

import java.io.InputStream;
import java.io.OutputStream;

import edu.umn.msi.tropix.common.execution.process.ProcessConfiguration;

/**
 * A configuration bean used to describe process executions to be
 * launched by instances of the {@link ExecutionFactory} class.
 * 
 * @author John Chilton
 *
 */
public class ExecutionConfiguration extends ProcessConfiguration {
  private OutputStream standardOutputStream = null;
  private OutputStream standardErrorStream = null;
  private InputStream standardInputStream = null;
  /**
   * If timeout is null or negative, this is equivalent to no timeout.
   */
  private Long timeout = null;

  public void setTimeout(final Long timeout) {
    this.timeout = timeout;
  }

  public Long getTimeout() {
    return this.timeout;
  }

  public OutputStream getStandardOutputStream() {
    return this.standardOutputStream;
  }

  public void setStandardOutputStream(final OutputStream standardOutputStream) {
    this.standardOutputStream = standardOutputStream;
  }

  public OutputStream getStandardErrorStream() {
    return this.standardErrorStream;
  }

  public void setStandardErrorStream(final OutputStream standardErrorStream) {
    this.standardErrorStream = standardErrorStream;
  }

  public InputStream getStandardInputStream() {
    return this.standardInputStream;
  }

  public void setStandardInputStream(final InputStream standardInputStream) {
    this.standardInputStream = standardInputStream;
  }
}

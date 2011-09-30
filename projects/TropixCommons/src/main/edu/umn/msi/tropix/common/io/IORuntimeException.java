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

package edu.umn.msi.tropix.common.io;

import java.io.IOException;

/**
 * A runtime exception that wraps an {@link IOException}. Checked exceptions
 * are a failed experiment and all Tropix code should wrap {@link IOException}s with this
 * class as quickly as possible to enable clearer programming at higher levels of
 * abstraction.
 * 
 * @author John Chilton
 *
 */
public class IORuntimeException extends RuntimeException {
  private static final long serialVersionUID = -2201870917446940056L;
  private final IOException ioException;

  public IORuntimeException(final IOException ioException) {
    super(ioException);
    this.ioException = ioException;
  }

  /**
   * @return The wrapped {@link IOException}.
   */
  public IOException getIOException() {
    return this.ioException;
  }
}

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

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Wraps a {@link DataInputStream} to provide methods for reading {@code double}s
 * and {@code float}s without dealing with checked exceptions.
 * 
 * @author John Chilton
 *
 */
public class FloatingPointDataInputStream {
  private DataInputStream dis = null;

  public FloatingPointDataInputStream(final InputStream inputStream) {
    this.dis = new DataInputStream(inputStream);
  }

  public double readDouble() {
    try {
      return this.dis.readDouble();
    } catch(final IOException e) {
      throw new IORuntimeException(e);
    }
  }

  public float readFloat() {
    try {
      return this.dis.readFloat();
    } catch(final IOException e) {
      throw new IORuntimeException(e);
    }
  }
}

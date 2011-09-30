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

package edu.umn.msi.tropix.common.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.input.ClosedInputStream;
import org.apache.commons.io.output.ClosedOutputStream;
import org.apache.commons.math.util.MathUtils;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.io.FloatingPointDataInputStream;
import edu.umn.msi.tropix.common.io.FloatingPointDataOutputStream;
import edu.umn.msi.tropix.common.io.IORuntimeException;

public class FloatingPointDataStreamTest {
  private ClosedInputStream closedInputStream = new ClosedInputStream();
  private ClosedOutputStream closedOutputStream = new ClosedOutputStream();

  @Test(groups = "unit")
  public void readDouble() throws IOException {
    final ByteArrayOutputStream bos = new ByteArrayOutputStream();
    // final DataOutputStream dos = new DataOutputStream(bos);
    final FloatingPointDataOutputStream dos = new FloatingPointDataOutputStream(bos);
    dos.writeDouble(123.45d);
    dos.writeFloat(12.3f);
    bos.close();
    final InputStream ds = new ByteArrayInputStream(bos.toByteArray());
    final FloatingPointDataInputStream fis = new FloatingPointDataInputStream(ds);
    final double d1 = fis.readDouble();
    final float f1 = fis.readFloat();
    assert MathUtils.equals(123.45, d1);
    assert MathUtils.equals(f1, 12.3f);
  }
  

  @Test(groups = "unit", expectedExceptions = IORuntimeException.class)
  public void readDoubleException() {
    new FloatingPointDataInputStream(this.closedInputStream).readDouble();
  }

  @Test(groups = "unit", expectedExceptions = IORuntimeException.class)
  public void readFloatException() {
    new FloatingPointDataInputStream(this.closedInputStream).readFloat();
  }

  @Test(groups = "unit", expectedExceptions = IORuntimeException.class)
  public void writeDoubleException() {
    new FloatingPointDataOutputStream(this.closedOutputStream).writeDouble(123.3d);
  }

  @Test(groups = "unit", expectedExceptions = IORuntimeException.class)
  public void writeFloatException() {
    new FloatingPointDataOutputStream(this.closedOutputStream).writeFloat(123.3f);
  }

}

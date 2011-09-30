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

package edu.umn.msi.tropix.common.logging;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.easymock.EasyMock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ExceptionUtilsTest {
  static enum Types {
    LOG_QUIETLY, LOG_AND_CONVERT, LOG_AND_RETHROW
  };

  private Log log;

  @Test(groups = "unit")
  public void toStringTest() {
    assert null == ExceptionUtils.toString(null);
    assert ExceptionUtils.toString(new IllegalStateException("Moo")).contains("IllegalStateException");
    assert ExceptionUtils.toString(new IllegalStateException("Moo")).contains("Moo");
  }

  @BeforeMethod(groups = "unit")
  public void init() {
    this.log = EasyMock.createMock(Log.class);
  }

  @Test(groups = "unit")
  public void logQuietlyThrowable() throws Throwable {
    this.logTest(false, null, Types.LOG_QUIETLY);
  }

  @Test(groups = "unit")
  public void logQuietlyThrowableAndMessage() throws Throwable {
    this.logTest(true, null, Types.LOG_QUIETLY);
  }

  @Test(groups = "unit")
  public void testLogAndConvert() throws Throwable {
    this.logTest(false, null, Types.LOG_AND_CONVERT);
  }

  @Test(groups = "unit")
  public void testLogAndConvertMessage() throws Throwable {
    this.logTest(true, null, Types.LOG_AND_CONVERT);
  }

  @Test(groups = "unit")
  public void testLogAndConvertMessageAndExceptionClass() {
    this.logTest(true, IllegalStateException.class, Types.LOG_AND_CONVERT);
  }

  @Test(groups = "unit")
  public void testLogAndConvertExceptionClass() {
    this.logTest(false, IllegalStateException.class, Types.LOG_AND_CONVERT);
  }

  @Test(groups = "unit", expectedExceptions = RuntimeException.class)
  public void testLogAndRethrow() throws Throwable {
    this.logTest(false, null, Types.LOG_AND_RETHROW);
  }

  @Test(groups = "unit", expectedExceptions = RuntimeException.class)
  public void testLogAndRethrowMessage() throws Throwable {
    this.logTest(true, null, Types.LOG_AND_RETHROW);
  }

  @Test(groups = "unit", expectedExceptions = IllegalArgumentException.class)
  public void testLogAndRethrowMessageAndExceptionClass() throws Throwable {
    this.logTest(true, IllegalArgumentException.class, Types.LOG_AND_RETHROW);
  }

  @Test(groups = "unit", expectedExceptions = IllegalStateException.class)
  public void testLogAndRethrowExceptionClass() throws Throwable {
    this.logTest(false, IllegalStateException.class, Types.LOG_AND_RETHROW);
  }

  public <T extends Throwable> void logTest(final boolean message, final Class<T> exceptionClass, final Types type) throws T {
    final Throwable t = new Throwable();
    final String messageStr = "Message";
    if(message) {
      this.log.warn(messageStr);
    }
    this.log.info("Exception Info : ", t);
    EasyMock.replay(this.log);
    try {
      if(type.equals(Types.LOG_QUIETLY)) {
        if(!message) {
          ExceptionUtils.logQuietly(this.log, t);
        } else {
          ExceptionUtils.logQuietly(this.log, t, "Message");
        }
      } else if(type.equals(Types.LOG_AND_CONVERT)) {
        if(!message && exceptionClass == null) {
          assert RuntimeException.class.isInstance(ExceptionUtils.logAndConvert(this.log, t));
        } else if(exceptionClass == null) {
          assert RuntimeException.class.isInstance(ExceptionUtils.logAndConvert(this.log, t, messageStr));
        } else if(message) {
          assert exceptionClass.isInstance(ExceptionUtils.logAndConvert(this.log, t, messageStr, exceptionClass));
        } else {
          assert exceptionClass.isInstance(ExceptionUtils.logAndConvert(this.log, t, exceptionClass));
        }
      } else if(type.equals(Types.LOG_AND_RETHROW)) {
        if(!message && exceptionClass == null) {
          ExceptionUtils.logAndRethrowUnchecked(this.log, t);
        } else if(exceptionClass == null) {
          ExceptionUtils.logAndRethrowUnchecked(this.log, t, messageStr);
        } else if(message) {
          ExceptionUtils.logAndRethrow(this.log, t, messageStr, exceptionClass);
        } else {
          ExceptionUtils.logAndRethrow(this.log, t, exceptionClass);
        }
      }
    } finally {
      EasyMock.verify(this.log);
    }
  }

  @Test(groups = "unit")
  public void convert() {
    final IOException ioException = new FileNotFoundException();

    final RuntimeException re = ExceptionUtils.convertException(ioException, RuntimeException.class);
    assert re.getCause().equals(ioException);
    final IllegalStateException ie = ExceptionUtils.convertException(ioException, "Moo", IllegalStateException.class);
    assert ie.getCause().equals(ioException) && ie.getMessage().equals("Moo");
    final FileNotFoundException fe = ExceptionUtils.convertException(ioException, FileNotFoundException.class);
    assert fe == ioException;

    ExceptionUtils.ExceptionConversionException ee = null;
    try {
      ExceptionUtils.convertException(new Throwable(), NullPointerException.class);
    } catch(final ExceptionUtils.ExceptionConversionException iee) {
      ee = iee;
    }
    assert ee != null;
  }
}

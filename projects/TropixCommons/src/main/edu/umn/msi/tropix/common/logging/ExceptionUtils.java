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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Constructor;

import javax.annotation.Nullable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Utility class for logging exceptions in a consistent manner throughout Tropix.
 * 
 * @author John Chilton
 *
 */
public class ExceptionUtils {
  private static final Log INTERNAL_LOG = LogFactory.getLog(ExceptionUtils.class);

  public static String toString(@Nullable final Throwable throwable) {
    if(throwable == null) {
      return null;
    }
    final StringWriter stackWriter = new StringWriter();
    throwable.printStackTrace(new PrintWriter(stackWriter));
    return stackWriter.toString();
  }

  public static void logQuietly(final Log log, final Throwable t) {
    ExceptionUtils.logQuietly(log, t, null);
  }

  public static void logQuietly(final Log log, final Throwable t, final String message) {
    if(message != null) {
      log.warn(message);
    }
    log.info("Exception Info : ", t);
  }

  public static RuntimeException logAndConvert(final Log log, final Throwable t) {
    ExceptionUtils.logQuietly(log, t);
    return ExceptionUtils.convertException(t);
  }

  public static RuntimeException logAndConvert(final Log log, final Throwable t, final String message) {
    ExceptionUtils.logQuietly(log, t, message);
    return ExceptionUtils.convertException(t, message);
  }

  public static <E extends Throwable> E logAndConvert(final Log log, final Throwable t, final Class<E> exceptionClass) {
    ExceptionUtils.logQuietly(log, t);
    return ExceptionUtils.convertException(t, exceptionClass);
  }

  public static <E extends Throwable> E logAndConvert(final Log log, final Throwable t, final String message, final Class<E> exceptionClass) {
    ExceptionUtils.logQuietly(log, t, message);
    return ExceptionUtils.convertException(t, message, exceptionClass);
  }

  public static void logAndRethrowUnchecked(final Log log, final Throwable t) {
    throw ExceptionUtils.logAndConvert(log, t);
  }

  public static void logAndRethrowUnchecked(final Log log, final Throwable t, final String message) {
    throw ExceptionUtils.logAndConvert(log, t, message);
  }

  public static <T extends Throwable> void logAndRethrow(final Log log, final Throwable t, final Class<T> exceptionClass) throws T {
    throw ExceptionUtils.logAndConvert(log, t, exceptionClass);
  }

  public static RuntimeException convertException(final Throwable t) {
    return ExceptionUtils.convertException(t, RuntimeException.class);
  }

  public static RuntimeException convertException(final Throwable t, final String message) {
    return ExceptionUtils.convertException(t, message, RuntimeException.class);
  }

  public static <E extends Throwable> E convertException(final Throwable t, final Class<E> exceptionClass) {
    return ExceptionUtils.convertException(t, null, exceptionClass);
  }

  /**
   * A {@link RuntimeException} indicating there was a problem converting some other exception instance
   * into a new class.
   * 
   * @author John Chilton
   *
   */
  public static class ExceptionConversionException extends RuntimeException {
    private static final long serialVersionUID = -3277907853218532825L;

    ExceptionConversionException(final Throwable cause) {
      super(cause);
    }
  }

  @SuppressWarnings("unchecked")
  public static <E extends Throwable> E convertException(final Throwable t, final String message, final Class<E> exceptionClass) {
    E exceptionInstance = null;
    try {
      if(message != null) {
        final Constructor<E> constructor = exceptionClass.getConstructor(String.class, Throwable.class);
        exceptionInstance = constructor.newInstance(message, t);
      } else if(exceptionClass.isInstance(t)) {
        exceptionInstance = (E) t;
      } else {
        final Constructor<E> constructor = exceptionClass.getConstructor(Throwable.class);
        exceptionInstance = constructor.newInstance(t);
      }
    } catch(final Exception exception) {
      ExceptionUtils.INTERNAL_LOG.warn("Failed to produce exception of class " + exceptionClass + " with message " + message + " and throwable " + t);
      ExceptionUtils.INTERNAL_LOG.info("Exception Info : " + exception);
      throw new ExceptionConversionException(exception);
    }
    return exceptionInstance;
  }

  public static <T extends Throwable> void logAndRethrow(final Log log, final Throwable t, final String message, final Class<T> exceptionClass) throws T {
    throw ExceptionUtils.logAndConvert(log, t, message, exceptionClass);
  }
}

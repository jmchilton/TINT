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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import net.jmchilton.concurrent.ObjectUtils;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.easymock.IArgumentMatcher;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;

import edu.umn.msi.tropix.common.io.FileUtilsFactory;

/**
 * Utility class with dozens of static methods to ease development using
 * the EasyMock mocking library.
 * 
 * @author John Chilton
 * 
 */
public class EasyMockUtils {

  public static <T> IAnswer<T> getNullAnswer() {
    return new IAnswer<T>() {
      public T answer() throws Throwable {
        return null;
      }
    };
  }

  public static <T> IAnswer<T> getConstantAnswer(final T ret) {
    return new IAnswer<T>() {
      public T answer() throws Throwable {
        return ret;
      }
    };
  }

  public static <T> IAnswer<T> waitFor(final Object object) {
    return EasyMockUtils.waitForAndAnswer(object, EasyMockUtils.<T>getNullAnswer());
  }

  public static <T> IAnswer<T> waitForAndReturn(final Object object, final T ret) {
    return EasyMockUtils.waitForAndAnswer(object, EasyMockUtils.<T>getConstantAnswer(ret));
  }

  public static <T> IAnswer<T> waitForAndAnswer(final Object object, final IAnswer<T> answer) {
    return new IAnswer<T>() {
      public T answer() throws Throwable {
        synchronized(object) {
          ObjectUtils.wait(object);
        }
        return answer.answer();
      }
    };
  }

  public static <T> Supplier<T> createMockSupplier() {
    @SuppressWarnings("unchecked")
    final Supplier<T> mock = EasyMock.createMock(Supplier.class);
    return mock;
  }

  public static <T, U> Function<T, U> createMockFunction() {
    @SuppressWarnings("unchecked")
    final Function<T, U> mock = EasyMock.createMock(Function.class);
    return mock;
  }

  public static InputStream inputStreamWithContents(final byte[] contents) {
    final ContentsArgumentMatcherImpl matcher = new ContentsArgumentMatcherImpl();
    matcher.expectedBytes = contents;
    EasyMock.reportMatcher(matcher);
    return null;
  }

  public static OutputStream copy(final InputStream inputStream) {
    final OutputStreamCopyArgumentMatcherImpl argMatcher = new OutputStreamCopyArgumentMatcherImpl();
    argMatcher.inputStream = inputStream;
    EasyMock.reportMatcher(argMatcher);
    return null;
  }

  public static InputStream copy(final OutputStream outputStream) {
    final InputStreamCopyArgumentMatcherImpl argMatcher = new InputStreamCopyArgumentMatcherImpl();
    argMatcher.outputStream = outputStream;
    EasyMock.reportMatcher(argMatcher);
    return null;
  }

  public static Writer copy(final Reader inputStream) {
    final WriterCopyArgumentMatcherImpl argMatcher = new WriterCopyArgumentMatcherImpl();
    argMatcher.reader = inputStream;
    EasyMock.reportMatcher(argMatcher);
    return null;
  }

  public static Reader copy(final Writer writer) {
    final ReaderCopyArgumentMatcherImpl argMatcher = new ReaderCopyArgumentMatcherImpl();
    argMatcher.writer = writer;
    EasyMock.reportMatcher(argMatcher);
    return null;
  }

  public static void verifyAndReset(final Object... mocks) {
    for(final Object mock : mocks) {
      EasyMock.verify(mock);
    }
    for(final Object mock : mocks) {
      EasyMock.reset(mock);
    }
  }

  abstract static class CopyArgumentMatcherImpl<T> implements IArgumentMatcher {
    private boolean failedToCopy;
    private T input;

    public void appendTo(final StringBuffer sb) {
      if(failedToCopy) {
        sb.append("Failed to copy");
      }
    }

    abstract void copy() throws IOException;

    @SuppressWarnings("unchecked")
    public boolean matches(final Object object) {
      this.setInput((T) object);
      try {
        copy();
      } catch(final IOException e) {
        this.failedToCopy = true;
        return false;
      }
      return true;
    }

    protected void setInput(final T input) {
      this.input = input;
    }

    protected T getInput() {
      return input;
    }

  }

  static class ContentsArgumentMatcherImpl implements IArgumentMatcher {
    private byte[] expectedBytes;

    // TODO:
    public void appendTo(final StringBuffer buffer) {
    }

    public boolean matches(final Object arg) {
      final InputStream input = (InputStream) arg;
      byte[] actualBytes = null;
      try {
        actualBytes = org.apache.commons.io.IOUtils.toByteArray(input);
      } catch(final IOException e) {
        e.printStackTrace();
        return false;
      }
      return Arrays.equals(expectedBytes, actualBytes);
    }

  }

  static class WriterCopyArgumentMatcherImpl extends CopyArgumentMatcherImpl<Writer> {
    private Reader reader;

    protected void copy() throws IOException {
      org.apache.commons.io.IOUtils.copy(reader, getInput());
    }

  }

  static class OutputStreamCopyArgumentMatcherImpl extends CopyArgumentMatcherImpl<OutputStream> {
    private InputStream inputStream;

    protected void copy() throws IOException {
      org.apache.commons.io.IOUtils.copy(inputStream, getInput());
    }
  }

  static class ReaderCopyArgumentMatcherImpl extends CopyArgumentMatcherImpl<Reader> {
    private Writer writer;

    protected void copy() throws IOException {
      org.apache.commons.io.IOUtils.copy(getInput(), writer);
    }
  }

  static class InputStreamCopyArgumentMatcherImpl extends CopyArgumentMatcherImpl<InputStream> {
    private OutputStream outputStream;

    protected void copy() throws IOException {
      org.apache.commons.io.IOUtils.copy(getInput(), outputStream);
    }
  }

  static class ArySameArugmentMatcher<T> implements IArgumentMatcher {
    private final T[] array;
    private T[] queryArray;

    public ArySameArugmentMatcher(final T[] array) {
      this.array = array;
    }

    public void appendTo(final StringBuffer arg) {
      arg.append("expected=" + Arrays.toString(this.array) + ",found=" + Arrays.toString(this.queryArray));
    }

    @SuppressWarnings("unchecked")
    public boolean matches(final Object arg) {
      this.queryArray = (T[]) arg;
      boolean matches = this.queryArray.length == this.array.length;
      if(matches) {
        for(int i = 0; i < this.array.length; i++) {
          if(this.array[i] != this.queryArray[i]) {
            matches = false;
            break;
          }
        }
      }
      return matches;
    }
  }

  public static <T> T[] arySame(final T[] ary) {
    EasyMock.reportMatcher(new ArySameArugmentMatcher<T>(ary));
    return null;
  }

  private interface Action {
    void run(Object object);
  }

  private static class ReplayAction implements Action {
    public void run(final Object object) {
      EasyMock.replay(object);
    }
  }

  private static class ResetAction implements Action {
    public void run(final Object object) {
      EasyMock.reset(object);
    }
  }

  private static class VerifyAction implements Action {
    public void run(final Object object) {
      EasyMock.verify(object);
    }
  }

  public static void replayAll(final Object... mocks) {
    EasyMockUtils.preformAll(new ReplayAction(), mocks);
  }

  public static void verifyAndResetAll(final Object... mocks) {
    verifyAll(mocks);
    resetAll(mocks);
  }

  public static void resetAll(final Object... mocks) {
    EasyMockUtils.preformAll(new ResetAction(), mocks);
  }

  public static void verifyAll(final Object... mocks) {
    EasyMockUtils.preformAll(new VerifyAction(), mocks);
  }

  private static void preformAll(final Action action, final Object[] mocks) {
    for(final Object mock : mocks) {
      if(mock instanceof Collection<?>) {
        @SuppressWarnings("unchecked")
        final Object[] array = ((Collection) mock).toArray();
        EasyMockUtils.preformAll(action, array);
      } else if(mock instanceof Object[]) {
        EasyMockUtils.preformAll(action, (Object[]) mock);
      } else {
        action.run(mock);
      }
    }
  }

  static class PropertyArgumentMatcher implements IArgumentMatcher {
    private Map<String, Object> expectedProperties;
    private final Collection<String> invalidProperties = new LinkedList<String>();

    public void setProperties(final Map<String, Object> expectedProperties) {
      this.expectedProperties = expectedProperties;
    }

    public void appendTo(final StringBuffer buffer) {
      boolean first = true;
      if(!this.invalidProperties.isEmpty()) {
        buffer.append("Invalid properties found - ");
        for(final String property : this.invalidProperties) {
          if(first) {
            first = false;
          } else {
            buffer.append(", ");
          }
          buffer.append(property);
        }
      } else {
        buffer.append("valid bean");
      }
    }

    private void invalidProperty(final String property, final Object expected, final Object actual) {
      this.invalidProperties.add("<property{" + property + "}, expected{" + expected + "},actual{" + actual + "}>");
    }

    public boolean matches(final Object arg) {
      for(final Entry<String, Object> entry : this.expectedProperties.entrySet()) {
        final String key = entry.getKey();
        final String getter = "get" + key.substring(0, 1).toUpperCase() + key.substring(1);
        try {
          final Method method = arg.getClass().getMethod(getter);
          final Object expectedValue = entry.getValue();
          final Object value = method.invoke(arg);
          if(!expectedValue.equals(value)) {
            this.invalidProperty(key, expectedValue, value);
          }
        } catch(final Exception e) {
          this.invalidProperties.add("<property{" + key + "},exception{" + e + "}>");
        }
      }
      return this.invalidProperties.isEmpty();
    }

  }

  public static class Reference<T> implements Supplier<T> {
    private T object;

    public void set(final T object) {
      this.object = object;
    }

    public T get() {
      return this.object;
    }
  }

  public interface Recorder<T> {
    void record(T object);
  }

  public static <T> T record(final Reference<T> reference) {
    return EasyMockUtils.record(new Recorder<T>() {
      public void record(final T object) {
        reference.set(object);
      }
    });
  }

  public static <T> Reference<T> newReference() {
    return new Reference<T>();
  }

  public static <T> Capture<T> newCapture() {
    return new Capture<T>();
  }

  public static <T> T record(final Recorder<T> recorder) {
    final IArgumentMatcher argMatcher = new IArgumentMatcher() {
      public void appendTo(final StringBuffer buffer) {
      }

      @SuppressWarnings("unchecked")
      public boolean matches(final Object arg) {
        recorder.record((T) arg);
        return true;
      }
    };
    EasyMock.reportMatcher(argMatcher);
    return null;
  }

  private static class CollectionRecorder<T> implements Recorder<T> {
    private final Collection<T> collection;

    public CollectionRecorder(final Collection<T> collection) {
      this.collection = collection;
    }

    public void record(final T object) {
      this.collection.add(object);
    }
  }

  public static <T> T addToCollection(final Collection<T> collection) {
    return EasyMockUtils.record(new CollectionRecorder<T>(collection));
  }

  public static <T> T isBeanWithProperties(final Map<String, Object> properties) {
    final PropertyArgumentMatcher argMatcher = new PropertyArgumentMatcher();
    argMatcher.setProperties(properties);
    EasyMock.reportMatcher(argMatcher);
    return null;
  }

  public static File writeContentsToFile(final byte[] contents) {
    final FileArgumentMatcherImpl argMatcher = new FileArgumentMatcherImpl();
    argMatcher.contents = contents;
    EasyMock.reportMatcher(argMatcher);
    return null;
  }

  public static File isFileParent(final File descendent) {
    final FileArgumentMatcherImpl argMatcher = new FileArgumentMatcherImpl();
    argMatcher.descendent = descendent;
    EasyMock.reportMatcher(argMatcher);
    return null;
  }

  static class FileArgumentMatcherImpl implements IArgumentMatcher {
    private File descendent = null;
    private byte[] contents = null;

    public void appendTo(final StringBuffer buffer) {
      buffer.append("invalid file received");
    }

    public boolean matches(final Object arg) {
      final File file = (File) arg;
      if(contents != null) {
        FileUtilsFactory.getInstance().writeByteArrayToFile(file, contents);
      }
      boolean isFile = true;
      if(descendent != null) {
        isFile = file.getAbsolutePath().startsWith(descendent.getAbsolutePath());
      }
      return isFile;
    }

  }

  public static File isFileParent(final File descendent, final byte[] contents) {
    final FileArgumentMatcherImpl argMatcher = new FileArgumentMatcherImpl();
    argMatcher.descendent = descendent;
    argMatcher.contents = contents;
    EasyMock.reportMatcher(argMatcher);
    return null;
  }

  public static <T, S extends Iterable<T>> S hasSameUniqueElements(final Iterable<? extends T> iterable) {
    final IArgumentMatcher argMatcher = new IArgumentMatcher() {

      public void appendTo(final StringBuffer buffer) {
        buffer.append("Incorrect elements found");
      }

      public boolean matches(final Object arg) {
        @SuppressWarnings("unchecked")
        final Iterable<T> input = (S) arg;
        for(final T object : input) {
          if(!Iterables.contains(iterable, object)) {
            return false;
          }
        }
        for(final T object : iterable) {
          if(!Iterables.contains(input, object)) {
            return false;
          }
        }
        return true;
      }

    };
    EasyMock.reportMatcher(argMatcher);
    return null;
  }

}

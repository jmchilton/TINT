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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import net.jmchilton.concurrent.CountDownLatch;

import org.apache.commons.io.output.ClosedOutputStream;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import edu.umn.msi.tropix.common.collect.Closure;

public class EasyMockUtilsTest {

  interface ArrayInterface {
    void foo(StringBuffer[] array);
  }

  interface Foo {
    void foo();
  }

  interface Bar {
    String bar();
  }

  static class FooCaller {
    private Foo foo;

    public void callFoo() {
      this.foo.foo();
    }
  }

  static class BarCaller {
    private Bar bar;

    public String callBar() {
      return this.bar.bar();
    }
  }

  @Test(groups = "unit", timeOut = 1000)
  public void answerConstant() throws Throwable {
    assert EasyMockUtils.getConstantAnswer("Moo").answer().equals("Moo");
  }

  @Test(groups = "unit", timeOut = 1000)
  public void createMockFunction() {
    final Function<String, Integer> mock = EasyMockUtils.createMockFunction();
    EasyMock.expect(mock.apply("5")).andReturn(5);
    EasyMock.replay(mock);
    assert mock.apply("5").equals(5);
    EasyMock.verify(mock);
  }

  @Test(groups = "unit", timeOut = 1000)
  public void answerNull() {
    final Foo foo = EasyMock.createMock(Foo.class);
    final FooCaller caller = new FooCaller();
    caller.foo = foo;
    foo.foo();
    EasyMock.expectLastCall().andAnswer(new IAnswer<Object>() {
      public Object answer() throws Throwable {
        return null;
      }
    });
    EasyMock.replay(foo);
    caller.callFoo();
    EasyMock.verify(foo);
  }

  static class WaitRunnable implements Runnable {
    private FooCaller caller = new FooCaller();
    private boolean returned = false;
    private CountDownLatch endLatch = new CountDownLatch(1);

    public void run() {
      this.caller.callFoo();
      this.returned = true;
      endLatch.countDown();
    }
  }

  static class BarRunnable implements Runnable {
    private BarCaller caller = new BarCaller();
    private String value = null;
    private final CountDownLatch latch = new CountDownLatch(1);

    public void run() {
      this.value = this.caller.callBar();
      latch.countDown();
    }

    String getValue() {
      return value;
    }
  }

  @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = {"NN"}, justification = "Not actually mutating state.")
  @Test(groups = "unit", timeOut = 1000, invocationCount = 10)
  public void waitAndReturnAnswer() throws InterruptedException {
    final Bar bar = EasyMock.createMock(Bar.class);
    final BarRunnable runnable = new BarRunnable();
    runnable.caller.bar = bar;
    final Object object = new Object();

    synchronized(object) {
      EasyMock.expect(bar.bar()).andAnswer(EasyMockUtils.waitForAndReturn(object, "Moo"));
    }
    EasyMock.replay(bar);
    new Thread(runnable).start();
    Thread.sleep(10);
    assert runnable.value == null;
    while(runnable.value == null) {
      synchronized(object) { // Renotify in case waiting hasn't occurred yet.
        object.notifyAll();
      }
      Thread.sleep(1);
    }
    assert runnable.getValue().equals("Moo");
    EasyMock.verify(bar);
  }

  @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = {"NN"}, justification = "Not actually mutating state.")
  @Test(groups = "unit", timeOut = 1000, invocationCount = 10)
  public void waitingAnswer() throws InterruptedException {
    final Foo foo = EasyMock.createMock(Foo.class);
    EasyMock.makeThreadSafe(foo, true);
    final WaitRunnable runnable = new WaitRunnable();
    runnable.caller.foo = foo;
    final Object object = new Object();
    foo.foo();
    EasyMock.expectLastCall().andAnswer(EasyMockUtils.waitFor(object));
    EasyMock.replay(foo);
    new Thread(runnable).start();
    assert !runnable.returned;
    while(!runnable.returned) {
      synchronized(object) { // Renotify in case waiting hasn't occurred yet.
        object.notifyAll();
      }
      Thread.sleep(1);
    }
    EasyMock.verify(foo);
  }

  @Test(groups = "unit", timeOut = 1000)
  public void copyReader() {
    final StringWriter writer = new StringWriter();

    @SuppressWarnings("unchecked")
    final Closure<Reader> readerClosure = EasyMock.createMock(Closure.class);

    readerClosure.apply(EasyMockUtils.copy(writer));
    EasyMock.replay(readerClosure);

    readerClosure.apply(new StringReader("Moo Cow"));

    EasyMock.verify(readerClosure);
    assert writer.toString().equals("Moo Cow");

  }

  @Test(groups = "unit", timeOut = 1000)
  public void copyOutputStream() {
    final ByteArrayInputStream stream = new ByteArrayInputStream("Moo".getBytes());

    @SuppressWarnings("unchecked")
    final Closure<OutputStream> outputStreamClosure = EasyMock.createMock(Closure.class);

    outputStreamClosure.apply(EasyMockUtils.copy(stream));
    EasyMock.replay(outputStreamClosure);

    final ByteArrayOutputStream output = new ByteArrayOutputStream();
    outputStreamClosure.apply(output);

    EasyMock.verify(outputStreamClosure);
    assert new String(output.toByteArray()).equals("Moo");
  }

  @Test(groups = "unit", timeOut = 1000)
  public void copyInputStream() {
    final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    @SuppressWarnings("unchecked")
    final Closure<InputStream> inputStreamClosure = EasyMock.createMock(Closure.class);

    inputStreamClosure.apply(EasyMockUtils.copy(outputStream));
    EasyMock.replay(inputStreamClosure);

    inputStreamClosure.apply(new ByteArrayInputStream("Moo Cow".getBytes()));

    EasyMock.verify(inputStreamClosure);
    assert new String(outputStream.toByteArray()).equals("Moo Cow");
  }

  @Test(groups = "unit", timeOut = 1000)
  public void copyFail() {
    final ClosedOutputStream outputStream = new ClosedOutputStream();

    @SuppressWarnings("unchecked")
    final Closure<InputStream> inputStreamClosure = EasyMock.createMock(Closure.class);

    inputStreamClosure.apply(EasyMockUtils.copy(outputStream));
    EasyMock.replay(inputStreamClosure);

    AssertionError error = null;
    try {
      inputStreamClosure.apply(new ByteArrayInputStream("Moo Cow".getBytes()));
    } catch(final AssertionError e) {
      error = e;
    }
    assert error != null;
  }

  @Test(groups = "unit", timeOut = 1000)
  public void copyWriter() {
    final StringWriter writer = new StringWriter();

    @SuppressWarnings("unchecked")
    final Closure<Writer> writerClosure = EasyMock.createMock(Closure.class);

    writerClosure.apply(EasyMockUtils.copy(new StringReader("Moo Cow")));
    EasyMock.replay(writerClosure);

    writerClosure.apply(writer);
    EasyMock.verify(writerClosure);
    assert writer.toString().equals("Moo Cow");

  }

  @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "DMI", justification = "Not actually using hard coded files.")
  @Test(groups = "unit", timeOut = 1000)
  public void isFileParent() {
    @SuppressWarnings("unchecked")
    final Closure<File> fileClosure = EasyMock.createMock(Closure.class);
    fileClosure.apply(EasyMockUtils.isFileParent(new File("/tmp/1")));
    EasyMock.replay(fileClosure);
    fileClosure.apply(new File("/tmp/1/moo/cow"));
    EasyMock.verify(fileClosure);
    EasyMock.reset(fileClosure);

    fileClosure.apply(EasyMockUtils.isFileParent(new File("/tmp/1")));
    EasyMock.replay(fileClosure);
    AssertionError error = null;
    try {
      fileClosure.apply(new File("/tmp/2/moo/cow"));
    } catch(final AssertionError e) {
      error = e;
    }
    assert error != null;
  }

  @Test(groups = "unit", timeOut = 1000)
  public void isFileParentWithWrite() throws IOException {
    final File tempFile = File.createTempFile("tpxtst", "");
    try {
      @SuppressWarnings("unchecked")
      final Closure<File> fileClosure = EasyMock.createMock(Closure.class);
      fileClosure.apply(EasyMockUtils.isFileParent(tempFile.getParentFile(), "Moo Cow".getBytes()));
      EasyMock.replay(fileClosure);
      fileClosure.apply(tempFile);
      EasyMock.verify(fileClosure);
    } finally {
      assert tempFile.delete();
    }
  }

  @Test(groups = "unit", timeOut = 1000)
  public void hasSameUniqueElements() {
    @SuppressWarnings("unchecked")
    final Closure<Iterable<String>> closure = EasyMock.createMock(Closure.class);
    closure.apply(EasyMockUtils.<String, Iterable<String>>hasSameUniqueElements(Lists.newArrayList("moo", "cow", "moo", "moo")));
    EasyMock.replay(closure);
    closure.apply(Sets.newHashSet("moo", "cow"));
    EasyMock.verify(closure);
    EasyMock.reset(closure);

    closure.apply(EasyMockUtils.<String, Iterable<String>>hasSameUniqueElements(Lists.newArrayList("moo", "cow", "moox")));
    EasyMock.replay(closure);
    AssertionError error = null;
    try {
      closure.apply(Sets.newHashSet("moo", "cow"));
    } catch(final AssertionError e) {
      error = e;
    }
    assert error != null;
    EasyMock.reset(closure);

    closure.apply(EasyMockUtils.<String, Iterable<String>>hasSameUniqueElements(Lists.newArrayList("moo", "cow")));
    EasyMock.replay(closure);
    error = null;
    try {
      closure.apply(Sets.newHashSet("moo", "cow", "moox"));
    } catch(final AssertionError e) {
      error = e;
    }
    assert error != null;
    EasyMock.reset(closure);

  }

  @Test(groups = "unit", timeOut = 1000)
  public void arySame() {
    final StringBuffer buffer1 = new StringBuffer("1"), buffer2 = new StringBuffer("1");

    final StringBuffer[] array1 = new StringBuffer[] {buffer1, buffer2}, array2 = new StringBuffer[] {buffer1, buffer2};
    final ArrayInterface i = EasyMock.createMock(ArrayInterface.class);

    i.foo(EasyMockUtils.arySame(array1));
    EasyMock.replay(i);
    i.foo(array2);
    EasyMock.verify(i);
    EasyMock.reset(i);
    i.foo(EasyMockUtils.arySame(array1));
    EasyMock.replay(i);
    AssertionError e = null;
    try {
      i.foo(new StringBuffer[] {buffer1, buffer1});
    } catch(final AssertionError ie) {
      e = ie;
    }
    assert e != null;
  }

  @Test(groups = "unit", timeOut = 1000)
  public void newCapture() {
    assert EasyMockUtils.newCapture() != null;
  }

  static class SimpleBean {
    public int getSeven() {
      return 7;
    }

    public int getSix() {
      return 6;
    }

    public String getString() {
      return "string";
    }

    public String getException() {
      throw new RuntimeException();
    }
  }

  @Test(groups = "unit", timeOut = 1000)
  public void isBeanWithProperties() {
    @SuppressWarnings("unchecked")
    final Closure<SimpleBean> closure = EasyMock.createMock(Closure.class);

    final Map<String, Object> props = Maps.newHashMap();
    props.put("seven", 7);
    props.put("string", "string");
    closure.apply(EasyMockUtils.<SimpleBean>isBeanWithProperties(props));
    EasyMock.replay(closure);
    closure.apply(new SimpleBean());
    EasyMock.verify(closure);
    EasyMock.reset(closure);

    props.put("six", 7);
    closure.apply(EasyMockUtils.<SimpleBean>isBeanWithProperties(props));
    EasyMock.replay(closure);
    AssertionError error = null;
    try {
      closure.apply(new SimpleBean());
    } catch(final AssertionError e) {
      error = e;
    }
    assert error != null;
    EasyMock.reset(closure);

    props.remove("six");
    props.put("exception", "ignored");
    closure.apply(EasyMockUtils.<SimpleBean>isBeanWithProperties(props));
    EasyMock.replay(closure);
    error = null;
    try {
      closure.apply(new SimpleBean());
    } catch(final AssertionError e) {
      error = e;
    }
    assert error != null;
    EasyMock.reset(closure);

  }

  @Test(groups = "unit", timeOut = 1000)
  public void addToCollection() {
    @SuppressWarnings("unchecked")
    final Closure<String> closure = EasyMock.createMock(Closure.class);
    final List<String> list = Lists.newArrayList();
    closure.apply(EasyMockUtils.addToCollection(list));
    closure.apply(EasyMockUtils.addToCollection(list));
    EasyMock.replay(closure);
    closure.apply("1x");
    assert list.get(0) == "1x";
    closure.apply("foo");
    assert list.get(1) == "foo";
    EasyMock.verify(closure);
  }

}

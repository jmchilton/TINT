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

import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;

import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.io.LineCallback;
import edu.umn.msi.tropix.common.io.LineProcessor;
import edu.umn.msi.tropix.common.io.LineProcessors;
import edu.umn.msi.tropix.common.io.impl.LineProcessorSupplierImpl;

public class LineProcessorSupplierImplTest {

  static class Callback implements LineCallback {
    private List<String> lines = new LinkedList<String>();

    public synchronized void processLine(final String line) {
      this.lines.add(line);
      this.notifyAll();
    }

  }

  @Test(groups = "unit")
  public void test() throws IOException, InterruptedException {
    this.test(false);
  }

  @Test(groups = "unit")
  public void stopOnInterrupt() throws IOException, InterruptedException {
    this.test(true);
  }

  @Test(groups = "unit")
  public void defaultLineProcessor() throws IOException, InterruptedException {
    final PipedReader reader = new PipedReader();
    final PipedWriter writer = new PipedWriter();
    reader.connect(writer);

    final LineProcessor<Reader> processor = LineProcessors.getDefaultSupplier().get();
    final Callback callback = new Callback();
    final Thread t = new Thread(new Runnable() {
      public void run() {
        processor.processLines(reader, callback);
      }
    });
    t.start();
    writer.write("moo");
    writer.flush();
    Thread.sleep(10);
    assert callback.lines.isEmpty();
    synchronized(callback) {
      writer.write("a\nco");
      while(callback.lines.isEmpty()) {
        callback.wait();
      }
      assert callback.lines.get(0).startsWith("mooa");
    }
    Thread.sleep(10);
    synchronized(callback) {
      writer.write("w\nx");
      while(callback.lines.size() < 2) {
        callback.wait();
      }
      assert callback.lines.get(1).startsWith("cow");
    }
    processor.stop();
    t.join();

  }

  
  public void test(final boolean testInterruptStop) throws IOException, InterruptedException {
    final LineProcessorSupplierImpl supplier = new LineProcessorSupplierImpl();
    supplier.setSleepTme(2);
    supplier.setStopOnInterrupt(testInterruptStop);
    final PipedReader reader = new PipedReader();
    final PipedWriter writer = new PipedWriter();
    reader.connect(writer);

    final LineProcessor<Reader> processor = supplier.get();
    final Callback callback = new Callback();
    final Thread t = new Thread(new Runnable() {
      public void run() {
        processor.processLines(reader, callback);
      }
    });
    t.start();
    writer.write("moo");
    writer.flush();
    Thread.sleep(10);
    callback.lines.isEmpty();
    synchronized(callback) {
      writer.write("a\nco");
      while(callback.lines.isEmpty()) {
        callback.wait();
      }
      assert callback.lines.get(0).startsWith("mooa");
    }
    Thread.sleep(10);
    t.interrupt();
    if(testInterruptStop) {
      t.join();
      return;
    }
    synchronized(callback) {
      writer.write("w\nx");
      while(callback.lines.size() < 2) {  
        callback.wait();
      }
      assert callback.lines.get(1).startsWith("cow");
    }
    processor.stop();
    t.join();
  }

  @Test(groups = "unit")
  public void beanOps() {
    final LineProcessorSupplierImpl supplier = new LineProcessorSupplierImpl();
    supplier.setSleepTme(234);
    supplier.setStopOnInterrupt(true);
    assert supplier.getSleepTime() == 234;
    assert supplier.getStopOnInterrupt();
    supplier.setStopOnInterrupt(false);
    assert !supplier.getStopOnInterrupt();
  }
}

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

package edu.umn.msi.tropix.common.io.impl;

import java.io.Reader;
import java.util.Iterator;

import org.springframework.jmx.export.annotation.ManagedOperation;

import com.google.common.base.Function;
import com.google.common.base.Supplier;

import edu.umn.msi.tropix.common.io.LineCallback;
import edu.umn.msi.tropix.common.io.LineIterator;
import edu.umn.msi.tropix.common.io.LineProcessor;

public class LineProcessorSupplierImpl implements Supplier<LineProcessor<Reader>> {
  private int sleepTime = 1;
  private boolean stopOnInterrupt = false;
  private final Function<Reader, Iterator<String>> iterFunction = new Function<Reader, Iterator<String>>() {
    public Iterator<String> apply(final Reader reader) {
      return new LineIterator(reader);
    }
  };

  public LineProcessor<Reader> get() {
    final LineProcessor<Reader> lineProcessor = new LineProcessorImpl();
    return lineProcessor;
  }

  private class LineProcessorImpl implements LineProcessor<Reader> {
    private boolean stop = false;

    public void processLines(final Reader reader, final LineCallback lineCallback) {
      final Iterator<String> lineIterator = LineProcessorSupplierImpl.this.iterFunction.apply(reader);
      while(!this.stop) {
        if(!lineIterator.hasNext()) {
          try {
            Thread.sleep(LineProcessorSupplierImpl.this.sleepTime);
          } catch(final InterruptedException e) {
            if(LineProcessorSupplierImpl.this.stopOnInterrupt) {
              break;
            }
          }
          continue;
        }
        final String line = lineIterator.next();
        lineCallback.processLine(line);
      }
    }

    public void stop() {
      this.stop = true;
    }
  }

  @ManagedOperation
  public void setSleepTme(final int sleepTime) {
    this.sleepTime = sleepTime;
  }

  @ManagedOperation
  public void setStopOnInterrupt(final boolean stopOnInterrupt) {
    this.stopOnInterrupt = stopOnInterrupt;
  }

  @ManagedOperation
  public int getSleepTime() {
    return this.sleepTime;
  }

  @ManagedOperation
  public boolean getStopOnInterrupt() {
    return this.stopOnInterrupt;
  }
}
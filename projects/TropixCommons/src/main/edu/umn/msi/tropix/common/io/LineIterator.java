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
import java.io.Reader;
import java.util.Iterator;

import javax.annotation.WillNotClose;

/**
 * An {@link Iterator} of lines of text from a specified reader.
 * 
 * @author John Chilton
 *
 */
public class LineIterator implements Iterator<String> {
  private final StringBuffer buffer = new StringBuffer();
  private final Reader reader;

  public LineIterator(@WillNotClose final Reader reader) {
    this.reader = reader;
  }

  private int newLineIndex() {
    int newLineIndex = this.buffer.indexOf("\n");
    if(newLineIndex == -1) {
      newLineIndex = this.buffer.indexOf("\r");
    }
    return newLineIndex;
  }

  public synchronized boolean hasNext() {
    try {
      while(reader.ready()) {
        final int character = this.reader.read();
        if(character == -1) {
          break;
        }
        buffer.append(Character.valueOf((char) character));
      }
      return newLineIndex() >= 0;
    } catch(final IOException e) {
      throw new IllegalStateException(e);
    }
  }

  public synchronized String next() {
    final int newLineIndex = newLineIndex();
    if(newLineIndex == -1) {
      throw new IllegalStateException("line to read is not available");
    }
    final String line = buffer.substring(0, newLineIndex + 1);
    buffer.delete(0, newLineIndex + 1);
    return line;
  }

  public void remove() {
    throw new UnsupportedOperationException("Removing lines is not available.");
  }

}

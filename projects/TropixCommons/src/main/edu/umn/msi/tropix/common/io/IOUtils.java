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

import java.io.Closeable;
import java.io.Flushable;
import java.io.InputStream;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

import javax.annotation.Nullable;

import edu.umn.msi.tropix.common.annotation.EliminatesStaticCling;

/**
 * A wrapper around Apache Common IO IOUtils class that uses Java 5+ concepts (such as {@link Closeable})
 * and doesn't use static methods or checked exceptions.
 * 
 * @author John Chilton
 * 
 */
@EliminatesStaticCling
public interface IOUtils {
  void closeQuietly(@Nullable Closeable closeable);

  void flush(Flushable flushable);

  void flushQuietly(@Nullable Flushable flushable);

  long copy(InputStream input, OutputStream output);

  long copy(Reader input, Writer output);

  long copyLarge(InputStream input, OutputStream output);

  long copyLarge(Reader input, Writer output);

  String toString(InputStream input);

  String toString(Reader input);

  String readLine(LineNumberReader lineNumberReader);

  Writer append(Writer writer, CharSequence charSequence);

  /**
   * Attempts to determine if the given stream corresponds to a Zip file.
   * 
   * @param inputStream
   */
  boolean isZippedStream(InputStream inputStream);

  byte[] toByteArray(InputStream inputStream);
  
  int read(InputStream inputStream, byte[] bytes);
  
}

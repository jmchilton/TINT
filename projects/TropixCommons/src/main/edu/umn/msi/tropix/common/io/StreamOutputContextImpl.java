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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.annotation.WillNotClose;

/**
 * Abstract base class for {@link OutputContext} classes, concrete sub classes
 * need only implement {@link #put(InputStream)}.
 * 
 * @author John Chilton
 *
 */
public abstract class StreamOutputContextImpl implements OutputContext {
  private static final FileUtils FILE_UTILS = FileUtilsFactory.getInstance();
  private static final IOUtils IO_UTILS = IOUtilsFactory.getInstance();

  public void put(final File file) {
    put(file, this);
  }

  public void put(final URL url) {
    put(url, this);
  }

  public static void put(final URL url, final OutputToStreamContext outputContext) {
    InputStream urlInputStream = null;
    try {
      urlInputStream = url.openStream();
      outputContext.put(urlInputStream);
    } catch(final IOException e) {
      throw new IORuntimeException(e);
    } finally {
      IO_UTILS.closeQuietly(urlInputStream);
    }    
  }

  public static void put(final byte[] bytes, final OutputToStreamContext outputContext) {    
    outputContext.put(new ByteArrayInputStream(bytes));
  }

  public void put(final byte[] bytes) {
    put(bytes, this);
  }
  
  public static void put(final File file, final OutputToStreamContext outputContext) {
    final InputStream fileInputStream = FILE_UTILS.getFileInputStream(file);
    try {
      outputContext.put(fileInputStream);
    } finally {
      IO_UTILS.closeQuietly(fileInputStream);
    }    
  }
  
  public abstract void put(@WillNotClose InputStream inputStream);
}

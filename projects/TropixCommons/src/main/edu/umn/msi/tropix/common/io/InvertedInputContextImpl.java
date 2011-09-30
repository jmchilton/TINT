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

import java.io.File;
import java.io.OutputStream;

/**
 * A base class for creating InputContext objects, a subclass need only
 * to implement {@link #get(OutputContext)}.
 *  
 * @author John Chilton
 *
 */
public abstract class InvertedInputContextImpl implements InputContext {
  private static final FileUtils FILE_UTILS = FileUtilsFactory.getInstance();
  private static final IOUtils IO_UTILS = IOUtilsFactory.getInstance();

  public abstract void get(OutputContext outputContext);

  public void get(final File file) {
    final OutputStream fileOutputStream = FILE_UTILS.getFileOutputStream(file);
    try {
      this.get(fileOutputStream);
    } finally {
      IO_UTILS.closeQuietly(fileOutputStream);
    }
  }

  public void get(final OutputStream outputStream) {
    this.get(OutputContexts.forOutputStream(outputStream));
  }
}

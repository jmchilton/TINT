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

import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Executor;

import edu.umn.msi.tropix.common.concurrent.Executors;
import edu.umn.msi.tropix.common.io.IOUtils;
import edu.umn.msi.tropix.common.io.IOUtilsFactory;
import edu.umn.msi.tropix.common.io.StreamCopier;

/**
 * TODO: Give more thought about how to recover and if that is even necessary for our applications.
 * 
 * @author John Chilton (chilton at msi dot umn dot edu)
 * 
 */
public class AsyncStreamCopierImpl implements StreamCopier {
  private Executor executor = Executors.getNewThreadExecutor();
  private IOUtils ioUtils = IOUtilsFactory.getInstance();

  public void copy(final InputStream inputStream, final OutputStream outputStream, final boolean closeOutputWhenComplete) {
    final Runnable runnable = new Runnable() {
      public void run() {
        try {
          AsyncStreamCopierImpl.this.ioUtils.copy(inputStream, outputStream);
        } finally {
          if(closeOutputWhenComplete) {
            AsyncStreamCopierImpl.this.ioUtils.closeQuietly(outputStream);
          }
        }
      }

      public String toString() {
        return "Stream Copier";
      }
    };
    this.executor.execute(runnable);
  }

  public void setExecutor(final Executor executor) {
    this.executor = executor;
  }

  public void setIOUtils(final IOUtils ioUtils) {
    this.ioUtils = ioUtils;
  }
}
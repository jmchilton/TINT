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

package edu.umn.msi.tropix.grid.gridftp;

import java.io.InputStream;
import java.io.OutputStream;

import edu.umn.msi.tropix.common.io.InputContext;
import edu.umn.msi.tropix.common.io.OutputContext;
import edu.umn.msi.tropix.common.io.StreamInputContextImpl;
import edu.umn.msi.tropix.common.io.StreamOutputContextImpl;
import edu.umn.msi.tropix.common.logging.ExceptionUtils;
import edu.umn.msi.tropix.grid.gridftp.GridFtpClient.GridFtpFile;

public class GridFtpClientUtils {
  public static InputContext getInputContext(final GridFtpClient client, final String filename) {
    return new StreamInputContextImpl() {
      public void get(final OutputStream outputStream) {
        try {
          client.get(filename, outputStream);
        } catch(final Exception e) {
          throw ExceptionUtils.convertException(e);
        }
      }
    };
  }

  public static OutputContext getOutputContext(final GridFtpClient client, final String filename) {
    return new StreamOutputContextImpl() {
      public void put(final InputStream inputStream) {
        client.put(filename, inputStream);
      }
    };
  }

  public static void deleteDirectoryRecursively(final GridFtpClient client, final String directory) {
    final Iterable<GridFtpFile> contents = client.list(directory);
    for(final GridFtpFile file : contents) {
      if(file.isDirectory()) {
        deleteDirectoryRecursively(client, file.getPath());
      } else {
        client.deleteFile(file.getPath());
      }
    }
    client.deleteDir(directory);
  }

}

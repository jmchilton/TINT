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

import javax.annotation.concurrent.Immutable;

public interface GridFtpClient {
  void deleteDir(String dir);

  void deleteFile(String filename);

  boolean exists(String filename);

  void makeDir(String dir);

  void get(String fileName, OutputStream outputStream);

  void put(String fileName, InputStream outputStream);

  Iterable<GridFtpFile> list(String dir);

  @Immutable
  public static class GridFtpFile {
    private final String path;
    private final boolean directory;

    public GridFtpFile(final String path, final boolean directory) {
      this.path = path;
      this.directory = directory;
    }

    public String getPath() {
      return path;
    }

    public boolean isDirectory() {
      return directory;
    }

  }

}

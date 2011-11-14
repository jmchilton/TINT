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

package edu.umn.msi.tropix.storage.core.access.fs;

import java.io.File;

import org.apache.commons.codec.digest.DigestUtils;

import com.google.common.base.Function;

public class DirectoryTreeFileFunctionImpl implements Function<String, File> {
  private File directory;
  private int width = 3; // Num hex characters to use (16^3 = ~4000 subdirectories)
  private int depth = 0;

  public void setTreeDepth(final int depth) {
    if(depth < 0) {
      throw new IllegalArgumentException("Depth cannot be less than 0");
    }
    this.depth = depth;
  }

  public void setTreeWidth(final int treeWidth) {
    if(treeWidth < 1 || treeWidth > 20) {
      throw new IllegalArgumentException("Width must be between 1 and 20.");
    }
    this.width = treeWidth;
  }

  public File apply(final String id) {
    byte[] bytes = id.getBytes();
    File binDirectory = directory;
    for(int currentDepth = 0; currentDepth < depth; currentDepth++) {
      bytes = DigestUtils.sha(bytes);
      binDirectory = new File(binDirectory, encode(bytes, width));
    }
    return new File(binDirectory, id);
  }

  public void setDirectory(final String directory) {
    this.setDirectory(new File(directory));
  }

  public void setDirectory(final File directory) {
    this.directory = directory;
  }

  private static byte encodeByte(final int binaryByte) {
    return (byte) ((binaryByte < 10) ? ('0' + binaryByte) : (('a' - 10) + binaryByte));
  }

  private static String encode(final byte[] binary, final int width) {
    final byte[] hexBinary = new byte[width];
    for(int i = 0; i < width; i++) {
      if(i % 2 == 0) {
        hexBinary[i] = encodeByte((binary[i / 2] & 0xf0) >>> 4);
      } else {
        hexBinary[i] = encodeByte(binary[(i - 1) / 2] & 0x0f);
      }
    }
    return new String(hexBinary);
  }
}

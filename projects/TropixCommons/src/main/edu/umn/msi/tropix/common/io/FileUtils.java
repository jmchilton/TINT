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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;

import javax.annotation.Nullable;
import javax.annotation.WillClose;

import edu.umn.msi.tropix.common.annotation.EliminatesStaticCling;

/**
 * A wrapper around Apache Commons IO FileUtils class that uses generics and doesn't use
 * static methods or checked exceptions.
 * 
 * @author John Chilton
 * 
 */
@EliminatesStaticCling
public interface FileUtils {

  /**
   * @return A File corresponding to a new directory in java.io.tmpdir. The directory will
   *         be deleted when this file object is garbage collected.
   */
  File createTempDirectory();

  File createTempFile();

  void moveFile(File sourceFile, File destFile);

  byte[] readFileToByteArray(String path);

  byte[] readFileToByteArray(File file);

  void writeStringToFile(File file, String data);

  void writeStringToFile(String path, String data);

  void writeByteArrayToFile(File file, byte[] contents);

  void writeByteArrayToFile(String path, byte[] contents);

  void writeStreamToFile(File file, @WillClose InputStream inputStream);

  void writeStreamToFile(String path, @WillClose InputStream inputStream);

  Collection<File> listFiles(File directory);

  Collection<File> listFiles(String directoryPath);

  Collection<File> listFiles(File directory, String[] extensions, boolean recursive);

  Collection<File> listFiles(String directoryPath, String[] extensions, boolean recursive);

  Iterator<File> iterateFiles(File directory, String[] extension, boolean recursive);

  void touch(File file);

  void touch(String path);

  boolean mkdir(File file);

  boolean mkdir(String path);

  boolean mkdirs(File file);

  boolean mkdirs(String path);

  void deleteDirectory(File directory);

  void deleteDirectory(String directoryPath);

  boolean deleteQuietly(@Nullable File file);

  boolean deleteQuietly(@Nullable String path);

  boolean deleteDirectoryQuietly(@Nullable File file);

  boolean deleteDirectoryQuietly(@Nullable String path);

  FileInputStream getFileInputStream(File file);

  FileInputStream getFileInputStream(String path);

  FileReader getFileReader(File file);

  FileReader getFileReader(String path);

  FileWriter getFileWriter(File file);

  FileWriter getFileWriter(String path);

  FileOutputStream getFileOutputStream(File file);

  FileOutputStream getFileOutputStream(String path);

  File createTempFile(String prefix, String suffix);

  String readFileToString(File file);

  String readFileToString(String path);

  void copyFile(File fromFile, File toFile);

}

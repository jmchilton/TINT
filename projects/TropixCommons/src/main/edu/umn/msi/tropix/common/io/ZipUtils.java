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
import java.io.InputStream;
import java.io.OutputStream;

import javax.annotation.WillClose;
import javax.annotation.WillNotClose;

import com.google.common.base.Function;

public interface ZipUtils {
  void zipToStream(Iterable<InputStream> inputStreams, Iterable<String> names, OutputStream outputStream);

  void zipContextsToStream(Iterable<? extends InputContext> inputStreams, Iterable<String> names, OutputStream outputStream);

  void unzip(File zipFile, Function<? super String, File> outputFileFunction);

  void unzipToContexts(File tempFile, Function<? super String, OutputContext> constant);

  void unzipToDirectory(File file, File parentFile);

  File unzipToTempDirectory(final File zipFile);

  File unzipToTempDirectory(@WillClose final InputStream inputStream);

  void zipDirectory(Directory directory, String resourcePath, @WillNotClose OutputStream outputStream);
}

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
import java.net.URL;

import javax.annotation.WillNotClose;

/**
 * Interface for inverting how {@link OutputStream} and classes like it
 * are dealt with. Instead of writing to an {@link OutputStream}, you tell
 * an {@link OutputContext} where you want to put data. This makes testing easier
 * and allows higher level IO programming (no need to deal with the details of
 * how to copy streams, etc...).
 *  
 * @author John Chilton
 *
 */
public interface OutputContext extends OutputToStreamContext {

  void put(File file);

  void put(byte[] bytes);

  /**
   * Populates the contents of the resource defined by this OutputContext with the 
   * contents of the supplied InputStream. This OutputContext will not close the InputStream.
   * 
   * @param inputStream
   */
  void put(@WillNotClose InputStream inputStream);

  void put(URL url);

}

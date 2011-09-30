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

import javax.annotation.Nullable;

/**
 * A super interface for the {@link StagingDirectory} interface, that describes various generic
 * operations on directories. 
 * 
 * @author John Chilton
 *
 */
public interface Directory {

  String getAbsolutePath();

  void makeDirectory(String resourcePath);

  InputContext getInputContext(String resourcePath);

  OutputContext getOutputContext(String resourcePath);

  /**
   * @param resourcePath
   *          - Path of subdirectory to list the resources in. If this null, the top directory defined by the staging directory will be searched.
   * 
   * @return An Iterable over the names of the resources available in the directory specified by resourcePath.
   */
  Iterable<String> getResourceNames(@Nullable String resourcePath);

  /**
   * @return The file separator for the resources defined by this directory.
   */
  String getSep();

}
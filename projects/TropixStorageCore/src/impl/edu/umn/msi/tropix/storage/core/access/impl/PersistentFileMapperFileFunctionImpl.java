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

package edu.umn.msi.tropix.storage.core.access.impl;

import java.io.File;

import com.google.common.base.Function;

import edu.umn.msi.tropix.storage.core.PersistentFileMapperService;

class PersistentFileMapperFileFunctionImpl implements Function<String, File> {
  private PersistentFileMapperService persistentFileMapperService;
  
  PersistentFileMapperFileFunctionImpl(final PersistentFileMapperService persistentFileMapperService) {
    this.persistentFileMapperService = persistentFileMapperService;
  }
  
  public File apply(String id) {
    final String path = persistentFileMapperService.getPath(id);
    return path == null ? null : new File(path);
  }

}

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

package edu.umn.msi.tropix.persistence.service;

import javax.annotation.Nullable;

import edu.umn.msi.tropix.models.IdentificationParameters;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.models.sequest.SequestParameters;
import edu.umn.msi.tropix.models.xtandem.XTandemParameters;
import edu.umn.msi.tropix.persistence.aop.Modifies;
import edu.umn.msi.tropix.persistence.aop.PersistenceMethod;
import edu.umn.msi.tropix.persistence.aop.UserId;

public interface ParametersService {

  @PersistenceMethod
  IdentificationParameters createSequestParameters(@UserId String userGridId, @Nullable @Modifies String folderId,
      IdentificationParameters parametersReference, SequestParameters parameters);

  @PersistenceMethod
  IdentificationParameters createXTandemParameters(@UserId String userGridId, @Nullable @Modifies String folderId,
      IdentificationParameters parametersReference, XTandemParameters parameters);

  @PersistenceMethod
  IdentificationParameters createXmlParameters(@UserId String userGridIdentity, @Nullable @Modifies String folderId,
      IdentificationParameters parametersReference, TropixFile file);

  SequestParameters loadSequestParameters(String id);

  XTandemParameters loadXTandemParameters(String id);

}

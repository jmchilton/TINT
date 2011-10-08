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

package edu.umn.msi.tropix.persistence.service.impl;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.inject.Named;

import edu.umn.msi.tropix.models.IdentificationParameters;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.models.sequest.SequestParameters;
import edu.umn.msi.tropix.models.utils.StockFileExtensionEnum;
import edu.umn.msi.tropix.models.xtandem.XTandemParameters;
import edu.umn.msi.tropix.persistence.dao.SequestParametersDao;
import edu.umn.msi.tropix.persistence.dao.XTandemParametersDao;
import edu.umn.msi.tropix.persistence.service.ParametersService;

// TODO: loadSequestParmaeters and XTandemParameters through IdentificationParametersId so we can do security checks...
@ManagedBean
@Named("parametersService")
class ParametersServiceImpl extends ServiceBase implements ParametersService {
  private SequestParametersDao sequestParametersDao;
  private XTandemParametersDao xTandemParametersDao;

  public SequestParameters loadSequestParameters(final String id) {
    return sequestParametersDao.load(id);
  }

  public XTandemParameters loadXTandemParameters(final String id) {
    return xTandemParametersDao.load(id);
  }

  public IdentificationParameters createSequestParameters(final String userGridId, final String folderId,
      final IdentificationParameters parametersReference, final SequestParameters parameters) {
    final String parametersId = sequestParametersDao.saveObject(parameters).getId();
    parametersReference.setType("SequestBean");
    parametersReference.setParametersId(parametersId);
    return createIdentificationParameters(userGridId, folderId, parametersReference);
  }

  public IdentificationParameters createXTandemParameters(final String userGridId, final String folderId,
      final IdentificationParameters parametersReference, final XTandemParameters parameters) {
    final String parametersId = xTandemParametersDao.saveObject(parameters).getId();
    parametersReference.setType("XTandemBean");
    parametersReference.setParametersId(parametersId);
    return createIdentificationParameters(userGridId, folderId, parametersReference);
  }

  protected IdentificationParameters createIdentificationParameters(final String userGridId, final String folderId,
      final IdentificationParameters parametersReference) {
    saveNewObjectToDestination(parametersReference, userGridId, folderId);
    return parametersReference;
  }

  @Inject
  public void setSequestParametersDao(final SequestParametersDao sequestParametersDao) {
    this.sequestParametersDao = sequestParametersDao;
  }

  @Inject
  public void setXTandemParametersDao(final XTandemParametersDao xTandemParametersDao) {
    this.xTandemParametersDao = xTandemParametersDao;
  }

  public IdentificationParameters createXmlParameters(final String userGridIdentity, final String folderId,
      final IdentificationParameters parametersReference, final TropixFile paramFile) {
    return saveXmlParameters(userGridIdentity, folderId, parametersReference, paramFile);
  }

  public IdentificationParameters createMyriMatchParameters(final String userGridIdentity, final String folderId,
      final IdentificationParameters parametersReference, final TropixFile paramFile) {
    return saveXmlParameters(userGridIdentity, folderId, parametersReference, paramFile);
  }

  private IdentificationParameters saveXmlParameters(final String userGridIdentity, final String folderId,
      final IdentificationParameters parametersReference, final TropixFile parameterFile) {

    parametersReference.setParametersId(null);
    final IdentificationParameters savedParameters = createIdentificationParameters(userGridIdentity, folderId, parametersReference);
    parameterFile.setFileType(getFileType(StockFileExtensionEnum.XML)); // getMessageSource().getMessage(MessageConstants.FILE_TYPE_OMSSA_INPUT));
    saveNewObjectWithParent(parameterFile, userGridIdentity, savedParameters.getId());
    // updateObjectWithParent(parameterFile, userGridIdentity, savedParameters.getId());
    savedParameters.setParametersId(parameterFile.getId());
    getTropixObjectDao().saveOrUpdateTropixObject(savedParameters);
    return savedParameters;
  }

}

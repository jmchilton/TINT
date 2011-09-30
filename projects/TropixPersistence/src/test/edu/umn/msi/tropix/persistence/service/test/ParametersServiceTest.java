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

package edu.umn.msi.tropix.persistence.service.test;

import javax.inject.Inject;

import org.springframework.test.annotation.NotTransactional;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.test.TestNGDataProviders;
import edu.umn.msi.tropix.models.IdentificationParameters;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.models.TropixObject;
import edu.umn.msi.tropix.models.User;
import edu.umn.msi.tropix.models.sequest.SequestParameters;
import edu.umn.msi.tropix.models.xtandem.XTandemParameters;
import edu.umn.msi.tropix.persistence.dao.Dao;
import edu.umn.msi.tropix.persistence.dao.SequestParametersDao;
import edu.umn.msi.tropix.persistence.service.FileService;
import edu.umn.msi.tropix.persistence.service.ParametersService;

public class ParametersServiceTest extends ServiceTest {
  @Inject
  private ParametersService parametersService;

  @Inject
  private SequestParametersDao sequestParametersDao;

  @Inject
  private FileService fileService;

  @Test(dataProvider = "bool1", dataProviderClass = TestNGDataProviders.class)
  public void createIdentificationXmlParameters(final boolean omssa) {
    final User user = createTempUser();
    for(final Destination destination : super.getTestDestinationsWithNull(user)) {
      final IdentificationParameters parametersReference = new IdentificationParameters();
      final String parameterType = omssa ? "OmssaXml" : "MyriMatch";
      parametersReference.setName("ParamTest");
      parametersReference.setType(parameterType);
      parametersReference.setCommitted(false);

      final String fileId = newId();
      final TropixFile file = new TropixFile();
      file.setCommitted(false);
      file.setFileId(fileId);

      IdentificationParameters returnedParameters;
      returnedParameters = parametersService.createXmlParameters(user.getCagridId(), destination.getId(), parametersReference, file);
      final IdentificationParameters loadedParameters = getTropixObjectDao().loadTropixObject(returnedParameters.getId(),
          IdentificationParameters.class);
      assert !loadedParameters.getCommitted();

      assert loadedParameters.getType().equals(parameterType);
      assert loadedParameters.getParametersId() != null;
      final TropixFile loadedFile = getTropixObjectDao().loadTropixObject(loadedParameters.getParametersId(), TropixFile.class);
      destination.validate(new TropixObject[] {loadedParameters, loadedFile});
      assert !loadedFile.getCommitted();
      assert loadedFile.getPermissionParents().contains(loadedParameters);
    }
  }

  @Test
  public void createXTandem() {
    final User user = createTempUser();
    // final Database database = saveNewCommitted(new Database(), user);
    for(final Destination destination : super.getTestDestinationsWithNull(user)) {
      final IdentificationParameters parametersReference = new IdentificationParameters();
      parametersReference.setCommitted(true);
      parametersReference.setName("ParamTest");
      final XTandemParameters parameters = new XTandemParameters();
      parameters.setOutputHistogramColumnWidth(30);
      final IdentificationParameters returnedParameters = parametersService.createXTandemParameters(user.getCagridId(), destination.getId(),
          parametersReference, parameters);

      final IdentificationParameters loadedParameters = getTropixObjectDao().loadTropixObject(returnedParameters.getId(),
          IdentificationParameters.class);
      destination.validate(new TropixObject[] {loadedParameters});
      assert loadedParameters.getType().equals("XTandemBean");
      assert loadedParameters.getName().equals("ParamTest");
      assert loadedParameters.getCommitted();
      final String parametersId = loadedParameters.getParametersId();
      final Dao<XTandemParameters> paramsDao = getDaoFactory().getDao(XTandemParameters.class);
      final XTandemParameters loadedXParameters = paramsDao.load(parametersId);
      assert loadedXParameters.getOutputHistogramColumnWidth().equals(30);
    }
  }

  @Test
  public void createSequest() {
    final User user = createTempUser();
    for(final Destination destination : super.getTestDestinationsWithNull(user)) {
      final IdentificationParameters parametersReference = new IdentificationParameters();
      parametersReference.setCommitted(true);
      parametersReference.setName("ParamTest");
      final SequestParameters parameters = new SequestParameters();
      parameters.setAddB(14d);
      final IdentificationParameters returnedParameters = parametersService.createSequestParameters(user.getCagridId(), destination.getId(),
          parametersReference, parameters);

      final IdentificationParameters loadedParameters = getTropixObjectDao().loadTropixObject(returnedParameters.getId(),
          IdentificationParameters.class);
      destination.validate(new TropixObject[] {loadedParameters});
      assert loadedParameters.getCommitted();
      assert loadedParameters.getType().equals("SequestBean");
      assert loadedParameters.getName().equals("ParamTest");
      final String parametersId = loadedParameters.getParametersId();
      final Dao<SequestParameters> paramsDao = getDaoFactory().getDao(SequestParameters.class);
      final SequestParameters loadedSParameters = paramsDao.load(parametersId);
      assert loadedSParameters.getAddB().equals(14d);
    }
  }

  @Test
  @NotTransactional
  public void outsideLoad() {
    final String id = createParameters();

    try {
      final SequestParameters parameters = parametersService.loadSequestParameters(id);
      assert parameters.getEnzymeName().equals("Trypsin");
    } finally {
      deleteParameters(id);
    }
  }

  private void deleteParameters(final String id) {
    sequestParametersDao.delete(id);
  }

  private String createParameters() {
    final String id = newId();
    final SequestParameters parameters = new SequestParameters();
    parameters.setEnzymeName("Trypsin");
    parameters.setId(id);
    sequestParametersDao.saveObject(parameters);
    return id;
  }

}

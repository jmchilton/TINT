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

package edu.umn.msi.tropix.webgui.server.download;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

import javax.annotation.ManagedBean;
import javax.inject.Inject;

import com.google.common.base.Function;

import edu.umn.msi.tropix.common.io.FileUtils;
import edu.umn.msi.tropix.common.io.FileUtilsFactory;
import edu.umn.msi.tropix.common.io.IOUtils;
import edu.umn.msi.tropix.common.io.IOUtilsFactory;
import edu.umn.msi.tropix.common.xml.XMLUtility;
import edu.umn.msi.tropix.files.PersistentModelStorageDataFactory;
import edu.umn.msi.tropix.models.ITraqQuantitationAnalysis;
import edu.umn.msi.tropix.models.utils.TropixObjectTypeEnum;
import edu.umn.msi.tropix.persistence.service.TropixObjectLoaderService;
import edu.umn.msi.tropix.proteomics.itraqquantitation.QuantificationResultsExporter;
import edu.umn.msi.tropix.proteomics.itraqquantitation.results.QuantificationResults;
import edu.umn.msi.tropix.storage.client.StorageData;
import edu.umn.msi.tropix.webgui.server.security.UserSession;

@ManagedBean
@FileDownloadHandlerType("quantification")
class ITraqQuantitationDownloadHandlerImpl implements FileDownloadHandler {
  private static final XMLUtility<QuantificationResults> RESULTS_XML_UTILITY = new XMLUtility<QuantificationResults>(QuantificationResults.class);
  private static final FileUtils FILE_UTILS = FileUtilsFactory.getInstance();
  private static final IOUtils IO_UTILS = IOUtilsFactory.getInstance();

  private final TropixObjectLoaderService tropixObjectService;
  private final UserSession userSession;
  private final PersistentModelStorageDataFactory storageDataFactory;

  @Inject
  ITraqQuantitationDownloadHandlerImpl(final TropixObjectLoaderService tropixObjectService, final UserSession userSession, final PersistentModelStorageDataFactory storageDataFactory) {
    this.tropixObjectService = tropixObjectService;
    this.userSession = userSession;
    this.storageDataFactory = storageDataFactory;
  }

  public void processDownloadRequest(final OutputStream stream, final Function<String, String> accessor) {
    final String quantificationAnalysisId = accessor.apply("id");
    final ITraqQuantitationAnalysis analysis = (ITraqQuantitationAnalysis) tropixObjectService.load(userSession.getGridId(), quantificationAnalysisId, TropixObjectTypeEnum.ITRAQ_QUANTITATION_ANALYSIS);
    final StorageData storageData = storageDataFactory.getStorageData(analysis.getOutput(), userSession.getProxy());
    final File tempFile = FILE_UTILS.createTempFile();
    try {
      storageData.getDownloadContext().get(tempFile);
      QuantificationResults results;
      // Try to deserialize the file, if it works its a new XML version of the results, otherwise
      // its an older pre xml variant and should just be copied as is.
      try {
        results = RESULTS_XML_UTILITY.deserialize(tempFile);
      } catch(final RuntimeException e) {
        results = null;
      }
      if(results != null) {
        QuantificationResultsExporter.writeAsSpreadsheet(results, stream);
      } else {
        final FileInputStream fileInputStream = FILE_UTILS.getFileInputStream(tempFile);
        try {
          IO_UTILS.copy(fileInputStream, stream);
        } finally {
          IO_UTILS.closeQuietly(fileInputStream);
        }
      }
    } finally {
      FILE_UTILS.deleteQuietly(tempFile);
    }
  }

}

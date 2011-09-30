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

package edu.umn.msi.tropix.grid.metadata.service;

import java.io.File;

import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.collect.Closure;
import edu.umn.msi.tropix.common.io.FileChangeMonitor;
import edu.umn.msi.tropix.common.io.FileUtils;
import edu.umn.msi.tropix.common.io.FileUtilsFactory;
import edu.umn.msi.tropix.common.io.InputContexts;
import edu.umn.msi.tropix.common.test.TestNGDataProviders;
import gov.nih.nci.cagrid.metadata.ServiceMetadata;
import gov.nih.nci.cagrid.metadata.ServiceMetadataHostingResearchCenter;
import gov.nih.nci.cagrid.metadata.ServiceMetadataServiceDescription;
import gov.nih.nci.cagrid.metadata.common.ResearchCenter;
import gov.nih.nci.cagrid.metadata.service.Service;

public class ServiceMetadataUpdaterTest {
  private static final FileUtils FILE_UTILS = FileUtilsFactory.getInstance();

  static class FileChangeMonitorImpl implements FileChangeMonitor {
    private File file;
    private Closure<File> closure;

    public void registerChangeListener(final File file, final Closure<File> listener) {
      this.file = file;
      this.closure = listener;
    }

  }

  @Test(groups = "unit", dataProvider="bool1", dataProviderClass=TestNGDataProviders.class)
  public void testUpdatesMetadata(final boolean writeFile) {
    final File tempFile = FILE_UTILS.createTempFile();
    try {
      final ServiceMetadataUpdater updater = new ServiceMetadataUpdater();
      final FileChangeMonitorImpl monitor = new FileChangeMonitorImpl();
      MetadataBeanImpl<ServiceMetadata> bean = new MetadataBeanImpl<ServiceMetadata>();
      final ServiceMetadata metadata = new ServiceMetadata();
      final ServiceMetadataServiceDescription serviceDescription = new ServiceMetadataServiceDescription();
      final Service service = new Service();
      serviceDescription.setService(service);
      metadata.setServiceDescription(serviceDescription);
      final ServiceMetadataHostingResearchCenter hostingResearchCenter = new ServiceMetadataHostingResearchCenter();
      final ResearchCenter researchCenter = new ResearchCenter();
      hostingResearchCenter.setResearchCenter(researchCenter);
      metadata.setHostingResearchCenter(hostingResearchCenter);
      bean.set(metadata);
      
      
      updater.setMetadataBean(bean);
      updater.setFileChangeMonitor(monitor);
      updater.setServiceName("moo");
      updater.setServiceDescription("cow");       
      if(writeFile) {
        InputContexts.forInputStream(getClass().getResourceAsStream("emptyResearchCenter.xml")).get(tempFile);
      } else {
        tempFile.delete();
      }
      updater.setResearchCenterFile(tempFile);
      
      updater.init();
      
      assert metadata.getServiceDescription().getService().getDescription().equals("cow");
      assert metadata.getServiceDescription().getService().getName().equals("moo");
      final String displayName = metadata.getHostingResearchCenter().getResearchCenter().getDisplayName(); 
      assert displayName.equals("");
      
      InputContexts.forInputStream(getClass().getResourceAsStream("namedResearchCenter.xml")).get(tempFile);
      monitor.closure.apply(tempFile);
      assert metadata.getHostingResearchCenter().getResearchCenter().getDisplayName().equals("Moo");
      
      
    } finally {
      FILE_UTILS.deleteQuietly(tempFile);
    }
  }



}

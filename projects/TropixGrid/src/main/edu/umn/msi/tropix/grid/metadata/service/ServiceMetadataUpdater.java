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

import javax.annotation.PostConstruct;

import org.springframework.util.StringUtils;

import edu.umn.msi.tropix.common.collect.Closure;
import edu.umn.msi.tropix.common.io.FileChangeMonitor;
import edu.umn.msi.tropix.common.io.OutputContexts;
import edu.umn.msi.tropix.grid.xml.SerializationUtils;
import edu.umn.msi.tropix.grid.xml.SerializationUtilsFactory;
import gov.nih.nci.cagrid.metadata.ServiceMetadata;
import gov.nih.nci.cagrid.metadata.ServiceMetadataHostingResearchCenter;
import gov.nih.nci.cagrid.metadata.common.ResearchCenter;

class ServiceMetadataUpdater {
  private static final SerializationUtils SERIALIZATION_UTILS = SerializationUtilsFactory.getInstance();
  private FileChangeMonitor fileChangeMonitor;
  private MetadataBeanImpl<ServiceMetadata> metadataBean;
  private String serviceName, serviceDescription;
  private File researchCenterFile;

  public void setMetadataBean(final MetadataBeanImpl<ServiceMetadata> metadataBean) {
    this.metadataBean = metadataBean;
  }

  public void setServiceName(final String serviceName) {
    this.serviceName = serviceName;
  }

  public void setServiceDescription(final String serviceDescription) {
    this.serviceDescription = serviceDescription;
  }

  public void setResearchCenterFile(final File researchCenterFile) {
    this.researchCenterFile = researchCenterFile;
    if(!researchCenterFile.exists()) {
      OutputContexts.forFile(researchCenterFile).put(getClass().getResource("emptyResearchCenter.xml"));
    }
  }

  private void update() {
    final ServiceMetadata serviceMetadata = metadataBean.get();
    if(StringUtils.hasText(serviceName)) {
      serviceMetadata.getServiceDescription().getService().setName(serviceName);
    }
    if(StringUtils.hasText(serviceDescription)) {
      serviceMetadata.getServiceDescription().getService().setDescription(serviceDescription);
    }
    if(researchCenterFile != null && researchCenterFile.exists()) {
      final ResearchCenter rc = SERIALIZATION_UTILS.deserialize(researchCenterFile, ResearchCenter.class);
      serviceMetadata.setHostingResearchCenter(new ServiceMetadataHostingResearchCenter(rc));
    }
    metadataBean.set(serviceMetadata);
  }

  @PostConstruct
  public void init() {
    if(fileChangeMonitor != null) {
      fileChangeMonitor.registerChangeListener(researchCenterFile, new Closure<File>() {
        public void apply(final File input) {
          update();
        }
      });
    }
    update();
  }

  public void setFileChangeMonitor(final FileChangeMonitor fileChangeMonitor) {
    this.fileChangeMonitor = fileChangeMonitor;
  }

}

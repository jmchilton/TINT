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
import java.net.MalformedURLException;
import java.net.URL;

import edu.umn.msi.tropix.common.io.FileUtils;
import edu.umn.msi.tropix.common.io.FileUtilsFactory;
import edu.umn.msi.tropix.common.io.InputContexts;
import gov.nih.nci.cagrid.metadata.common.ResearchCenter;

import org.testng.annotations.Test;

public class URLMetadataSetterTest {
  private static final FileUtils FILE_UTILS = FileUtilsFactory.getInstance();

  @Test(groups = "unit")
  public void testUpdate() throws MalformedURLException {
    final File tempFile = FILE_UTILS.createTempFile();
    try {
      final MetadataBeanImpl<ResearchCenter> metadataBean = new MetadataBeanImpl<ResearchCenter>();
    
      InputContexts.forInputStream(getClass().getResourceAsStream("emptyResearchCenter.xml")).get(tempFile);

      final URLMetadataSetter<ResearchCenter> setter = new URLMetadataSetter<ResearchCenter>(metadataBean, ResearchCenter.class, new URL("file:" + tempFile.getAbsolutePath()));

      assert metadataBean.get().getDisplayName().equals("");
      InputContexts.forInputStream(getClass().getResourceAsStream("namedResearchCenter.xml")).get(tempFile);
      setter.update();
      
      assert !metadataBean.get().getDisplayName().equals("");
                  
    } finally {
      FILE_UTILS.deleteQuietly(tempFile);
    }
  }
  
}

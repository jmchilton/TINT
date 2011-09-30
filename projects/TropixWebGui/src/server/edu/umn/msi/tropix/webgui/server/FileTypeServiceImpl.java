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

package edu.umn.msi.tropix.webgui.server;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.inject.Named;

import edu.umn.msi.tropix.models.FileType;
import edu.umn.msi.tropix.persistence.service.FileTypeService;
import edu.umn.msi.tropix.webgui.server.aop.ServiceMethod;
import edu.umn.msi.tropix.webgui.server.models.BeanSanitizer;
import edu.umn.msi.tropix.webgui.server.security.UserSession;

@ManagedBean @Named("gwtFileTypeService")
public class FileTypeServiceImpl implements edu.umn.msi.tropix.webgui.services.object.FileTypeService {
  private final UserSession userSession;
  private final BeanSanitizer beanSanitizer;
  private final FileTypeService fileTypeService;
  
  @Inject
  public FileTypeServiceImpl(final UserSession userSession, final BeanSanitizer beanSanitizer, final FileTypeService fileTypeService) {
    this.userSession = userSession;
    this.beanSanitizer = beanSanitizer;
    this.fileTypeService = fileTypeService;
  }

  @ServiceMethod(adminOnly = true)
  public FileType createFileType(final FileType fileType) {
    return beanSanitizer.sanitize(fileTypeService.create(userSession.getGridId(), fileType));
  }

  @ServiceMethod(adminOnly = true)
  public void updateFileType(final FileType fileType) {
    fileTypeService.update(userSession.getGridId(), fileType);
  }
  
}

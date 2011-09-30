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

package edu.umn.msi.tropix.client.request.actions.impl;

import javax.annotation.Nonnull;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "UPLOAD_ACTION")
public class UploadRequestAction extends RequestAction {
  @Nonnull
  @Column(name = "FILE_ID", nullable = false)
  private String fileId;

  @Nonnull
  @Column(name = "STORAGE_SERVICE", nullable = false)
  private String fileStorageService;

  @Nonnull
  @Column(name = "SERVICE_RESULT", nullable = false, columnDefinition = "longvarchar")
  private String serviceResultXml;

  protected UploadRequestAction() {

  }

  UploadRequestAction(@Nonnull final long actionId, @Nonnull final String requestService, @Nonnull final String requestId, @Nonnull final String fileId, @Nonnull final String fileStorageService, @Nonnull final String serviceResultXml) {
    super(actionId, requestService, requestId);
    this.fileId = fileId;
    this.fileStorageService = fileStorageService;
    this.serviceResultXml = serviceResultXml;
  }

  @Nonnull
  public String getServiceResultXml() {
    return serviceResultXml;
  }

  @Nonnull
  public String getFileId() {
    return fileId;
  }

  public String getFileStorageService() {
    return fileStorageService;
  }

}

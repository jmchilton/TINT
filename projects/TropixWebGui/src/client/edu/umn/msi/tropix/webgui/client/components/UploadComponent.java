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

package edu.umn.msi.tropix.webgui.client.components;

import java.util.List;

import javax.annotation.Nullable;

import com.smartgwt.client.widgets.Canvas;

public interface UploadComponent extends CanvasComponent<Canvas> {

  public final class CanUpload {
    private final boolean canUpload;
    private final String reason;
    
    private CanUpload(final boolean canUpload, @Nullable final String reason) {
      this.canUpload = canUpload;
      this.reason = reason;
    }
    
    public static CanUpload canUpload() {
      return new CanUpload(true, null);
    }
    
    public static CanUpload cannotUpload(final String reason) {
      return new CanUpload(false, reason);
    }
    
    @Nullable
    public String getReason() {
      return reason;
    }
    
    public boolean getCanUpload() {
      return canUpload;
    }
  }
  
  CanUpload canUpload();
  
  void startUpload();

  int getNumSelectedFiles();
  
  boolean isZip();

  boolean hasNames();

  List<String> getNames();

}

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

import java.util.LinkedHashMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface UploadComponentFactory<T extends UploadComponent> extends ComponentFactory<UploadComponentFactory.UploadComponentOptions, T> {

  public class UploadComponentOptions {
    public UploadComponentOptions(final boolean allowMultiple, @Nonnull final AsyncCallback<LinkedHashMap<String, String>> completionCallback) {
      this.allowMultiple = allowMultiple;
      this.completionCallback = completionCallback;
    }

    public UploadComponentOptions(@Nonnull final AsyncCallback<LinkedHashMap<String, String>> completionCallback) {
      this(false, completionCallback);
    }

    /**
     * Callback to reterive ids on completion.
     */
    private final AsyncCallback<LinkedHashMap<String, String>> completionCallback;
    private String types;
    private String typesDescription;
    private boolean allowMultiple;

    public void setAllowMultiple(final boolean allowMultiple) {
      this.allowMultiple = allowMultiple;
    }

    public boolean isAllowMultiple() {
      return allowMultiple;
    }

    @Nullable
    public String getTypes() {
      return types;
    }

    public void setTypes(@Nullable final String types) {
      this.types = types;
    }

    @Nonnull
    public AsyncCallback<LinkedHashMap<String, String>> getCompletionCallback() {
      return completionCallback;
    }

    @Nullable
    public String getTypesDescription() {
      return typesDescription;
    }
    
    public void setTypesDescription(@Nullable final String typesDescription) {
      this.typesDescription = typesDescription;
    }
  }

}

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
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.umn.msi.tropix.webgui.client.utils.Maps;

public interface UploadComponentFactory<T extends UploadComponent> extends ComponentFactory<UploadComponentFactory.UploadComponentOptions, T> {

  public class FileSource {
    private String id;
    
    private String name;
    
    private boolean upload;
    
    public FileSource(final String id, final String name, final boolean upload) {
      this.id = id;
      this.name = name;
      this.upload = upload;
    }
    
    
    public String getId() {
      return id;
    }

    public String getName() {
      return name;
    }

    public boolean isUpload() {
      return upload;
    }
    
  }
  
  public class UploadComponentOptions {
    
    public UploadComponentOptions(final boolean allowMultiple, @Nonnull final AsyncCallback<List<FileSource>> completionCallback) {
      this.allowMultiple = allowMultiple;
      this.completionCallback = completionCallback;
    }
    
    
    /* Same type earsure, eventually replace lower method with this one.
    public UploadComponentOptions(@Nonnull final AsyncCallback<List<FileSource>> completionCallback) {
      this(false, completionCallback);
    }
    */

    public UploadComponentOptions(@Nonnull final AsyncCallback<LinkedHashMap<String, String>> completionCallback) {
      this(false, convertCallback(completionCallback));
    }
    
    private static AsyncCallback<List<FileSource>> convertCallback(final AsyncCallback<LinkedHashMap<String, String>> completionCallback) {
      return new AsyncCallback<List<FileSource>>() {

        public void onFailure(final Throwable caught) {
          completionCallback.onFailure(caught);
        }

        public void onSuccess(final List<FileSource> result) {
          final LinkedHashMap<String, String> results = Maps.newLinkedHashMap();
          for(final FileSource fileSource : result) {
            results.put(fileSource.getName(), fileSource.getId());
          }
          completionCallback.onSuccess(results);
        }        
      };
    }

    /**
     * Callback to reterive ids on completion.
     */
    private final AsyncCallback<List<FileSource>> completionCallback;
    private String extension;
    private String types;
    private String typesDescription;
    private boolean allowMultiple;

    public void setAllowMultiple(final boolean allowMultiple) {
      this.allowMultiple = allowMultiple;
    }

    public boolean isAllowMultiple() {
      return allowMultiple;
    }
    
    public void setExtension(@Nullable final String extension) {
      this.extension = extension;
    }
    
    @Nullable
    public String getExtension() {
      return extension;
    }

    @Nullable
    public String getTypes() {
      return types;
    }

    public void setTypes(@Nullable final String types) {
      this.types = types;
    }

    @Nonnull
    public AsyncCallback<List<FileSource>> getCompletionCallback() {
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

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

package edu.umn.msi.tropix.webgui.client.components.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.common.base.Supplier;
import com.google.inject.Inject;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;

import edu.umn.msi.tropix.models.FileType;
import edu.umn.msi.tropix.webgui.client.Session;
import edu.umn.msi.tropix.webgui.client.components.FileTypeFormItemComponent;
import edu.umn.msi.tropix.webgui.client.utils.Maps;
import edu.umn.msi.tropix.webgui.client.utils.StringUtils;

public class FileTypeFormItemComponentSupplierImpl implements Supplier<FileTypeFormItemComponent> {
  private Session session;
  
  @Inject
  public FileTypeFormItemComponentSupplierImpl(final Session session) {
    this.session = session;
  }
    
  public FileTypeFormItemComponent get() {
    return new FileTypeFormItemComponentImpl(session);
  }
  
  private static class FileTypeFormItemComponentImpl implements FileTypeFormItemComponent {
    private final HashMap<String, FileType> fileTypes = new HashMap<String, FileType>();
    private final SelectItem selectItem;
    
    FileTypeFormItemComponentImpl(final Session session) {
      for(final FileType fileType : session.getFileTypes()) {
        fileTypes.put(fileType.getId(), fileType);      
      }
      selectItem = new SelectItem("fileType", "File Type");
      final LinkedHashMap<String, String> valueMap = Maps.newLinkedHashMap();
      for(final Map.Entry<String, FileType> fileTypeEntry : fileTypes.entrySet()) {
        final FileType fileType = fileTypeEntry.getValue();
        String fileTypeDescription;
        if(StringUtils.hasText(fileType.getExtension())) {
          if(StringUtils.hasText(fileType.getShortName())) {
            fileTypeDescription = fileType.getShortName() + " (" + fileType.getExtension() + ")";
          } else {
            fileTypeDescription = fileType.getExtension();
          }
        } else { // Must have an at least a shortname
          fileTypeDescription = fileType.getShortName();
        }
        valueMap.put(fileTypeEntry.getKey(), fileTypeDescription);
      }
      selectItem.setValueMap(valueMap);
    }
    
    public FileType getSelection() {
      final Object value = StringUtils.toString(selectItem.getValue());
      return value == null ? null : fileTypes.get(value.toString());
    }

    public void setSelection(final String extension) {
      for(final Map.Entry<String, FileType> fileTypeEntry : fileTypes.entrySet()) {
        if(fileTypeEntry.getValue().getExtension().equals(extension)) {
          selectItem.setValue(fileTypeEntry.getKey());
          break;
        }
      }
    }

    public FormItem get() {
      return selectItem;
    }
    
  }
}

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

import java.util.Collection;

import edu.umn.msi.tropix.webgui.client.components.tree.TreeItem;
import edu.umn.msi.tropix.webgui.client.utils.Listener;

public interface MetadataInputComponentFactory extends ComponentFactory<MetadataInputComponentFactory.MetadataOptions, MetadataInputComponent> {
  public class MetadataOptions {
    public enum DestinationType {
      ALL, FOLDER
    };

    private final String objectType;
    private Listener<Boolean> isValidListener;
    private DestinationType destinationType = DestinationType.ALL;
    private Collection<TreeItem> initialItems;

    public MetadataOptions(final String objectType) {
      this.objectType = objectType;
    }

    public Listener<Boolean> getIsValidListener() {
      return isValidListener;
    }

    public void setIsValidListener(final Listener<Boolean> isValidListener) {
      this.isValidListener = isValidListener;
    }

    public String getObjectType() {
      return objectType;
    }

    public DestinationType getDestinationType() {
      return destinationType;
    }

    public void setDestinationType(final DestinationType destinationType) {
      this.destinationType = destinationType;
    }

    public Collection<TreeItem> getInitialItems() {
      return initialItems;
    }

    public void setInitialItems(final Collection<TreeItem> initialItems) {
      this.initialItems = initialItems;
    }

  }
}

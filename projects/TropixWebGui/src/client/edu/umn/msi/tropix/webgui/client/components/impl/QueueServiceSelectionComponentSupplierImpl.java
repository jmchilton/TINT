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

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;

import edu.umn.msi.tropix.client.services.QueueGridService;

public class QueueServiceSelectionComponentSupplierImpl<T extends QueueGridService> extends ServiceSelectionComponentSupplierImpl<T> {

  @Override
  protected ServiceSelectionComponentImpl<T> getNewSelectionComponent() {
    return new QueueServiceSelectionComponentImpl<T>();
  }

  static class QueueServiceSelectionComponentImpl<T extends QueueGridService> extends ServiceSelectionComponentImpl<T> {

    protected static ListGridField getJobsField() {
      final ListGridField jobsField = new ListGridField("Jobs");
      jobsField.setType(ListGridFieldType.INTEGER);
      jobsField.setAlign(Alignment.LEFT);
      jobsField.setWidth(50);
      return jobsField;
    }

    @Override
    protected ListGridField[] getFields() {
      return new ListGridField[] {ServiceSelectionComponentImpl.getNameField(), QueueServiceSelectionComponentImpl.getJobsField(), ServiceSelectionComponentImpl.getHostField()};
    }

    @Override
    protected ListGridRecord getRecord(final T gridService) {
      if(!gridService.getActive()) {
        return null;
      }
      final ListGridRecord record = super.getRecord(gridService);
      record.setAttribute("Jobs", gridService.getJobsPending());
      return record;
    }

  }
}

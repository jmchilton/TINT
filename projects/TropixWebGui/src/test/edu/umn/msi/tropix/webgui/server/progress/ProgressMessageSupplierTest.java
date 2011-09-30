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

package edu.umn.msi.tropix.webgui.server.progress;

import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.test.BeanTest;
import edu.umn.msi.tropix.webgui.services.message.ProgressMessage;

public class ProgressMessageSupplierTest {

  @Test(groups = "unit")
  public void testBeanProperties() {
    BeanTest.testBeanProperties(new ProgressMessageSupplier());
  }
  
  @Test(groups = "unit")
  public void testProgressSupplier() {
    final ProgressMessageSupplier supplier = new ProgressMessageSupplier();
    supplier.setId("id");
    supplier.setJobStatus(ProgressMessage.JOB_COMPLETE);
    supplier.setName("name");
    supplier.setPercent(.45f);
    supplier.setStep("step");
    supplier.setStepStatus("stepStatus");
    supplier.setWorkflowId("workflowId");
    final ProgressMessage message = supplier.get();
    
    assert message.getId().equals("id");
    assert message.getJobStatus() == ProgressMessage.JOB_COMPLETE;
    assert message.getName().equals("name");
    assert message.getPercent() == .45f;
    assert message.getStep().equals("step");
    assert message.getStepStatus().equals("stepStatus");
    assert message.getWorkflowId().equals("workflowId");    
  }
  
  
  @Test(groups = "unit")
  public void testNullProgressSupplier() {
    final ProgressMessageSupplier supplier = new ProgressMessageSupplier();
    final ProgressMessage message = supplier.get();
    assert message.getId() == null;
    assert message.getJobStatus() == ProgressMessage.JOB_RUNNING;
    assert message.getName() == null;
    assert message.getPercent() == 0.0f;
    assert message.getStep().equals("");
    assert message.getStepPercent() == 0.0f;
    assert message.getStepStatus() == null;
    assert message.getWorkflowId() == null;    
  }
  
  
}

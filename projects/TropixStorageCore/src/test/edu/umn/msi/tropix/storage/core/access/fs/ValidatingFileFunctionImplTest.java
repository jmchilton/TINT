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

package edu.umn.msi.tropix.storage.core.access.fs;

import java.io.File;
import java.util.UUID;

import org.easymock.EasyMock;
import org.testng.annotations.Test;

import com.google.common.base.Function;

import edu.umn.msi.tropix.common.test.EasyMockUtils;
import edu.umn.msi.tropix.storage.core.access.fs.UuidValidator;
import edu.umn.msi.tropix.storage.core.access.fs.ValidatingFileFunctionImpl;

public class ValidatingFileFunctionImplTest {

  @Test(groups = "unit")
  public void apply() {
    final ValidatingFileFunctionImpl function = new ValidatingFileFunctionImpl();
    final UuidValidator uuidValidator = EasyMock.createMock(UuidValidator.class);
    function.setValidator(uuidValidator);
    final Function<String, File> wrappedFunction = EasyMockUtils.createMockFunction();
    function.setWrappedFileFunction(wrappedFunction);

    final String id1 = UUID.randomUUID().toString(), id2 = UUID.randomUUID().toString();

    EasyMock.expect(uuidValidator.apply(id1)).andReturn(true);
    EasyMock.expect(uuidValidator.apply(id2)).andReturn(false);
    EasyMock.expect(wrappedFunction.apply(id1)).andReturn(new File("moo1"));

    EasyMock.replay(uuidValidator, wrappedFunction);
    assert new File("moo1").equals(function.apply(id1));
    RuntimeException exception = null;
    try {
      function.apply(id2);
    } catch(final RuntimeException e) {
      exception = e;
    }
    assert exception != null;
    EasyMockUtils.verifyAndReset(uuidValidator, wrappedFunction);
  }
}

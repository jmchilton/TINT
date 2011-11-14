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

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

public class ValidatingFileFunctionImpl implements Function<String, File> {
  private Predicate<String> validator = Predicates.alwaysTrue();
  private Function<String, File> wrappedFileFunction = null;

  public File apply(final String id) {
    if(!validator.apply(id)) {
      throw new IllegalArgumentException("Id is not valid UUID -- id is " + id);
    }
    return wrappedFileFunction.apply(id);
  }

  public void setValidator(final Predicate<String> validator) {
    this.validator = validator;
  }

  public void setWrappedFileFunction(final Function<String, File> wrappedFileFunction) {
    this.wrappedFileFunction = wrappedFileFunction;
  }

}

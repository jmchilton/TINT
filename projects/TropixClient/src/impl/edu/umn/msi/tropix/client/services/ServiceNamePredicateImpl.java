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

package edu.umn.msi.tropix.client.services;

import java.util.Arrays;

import com.google.common.base.Predicate;

public class ServiceNamePredicateImpl implements Predicate<String> {
  private Iterable<String> validNames;

  public boolean apply(final String address) {
    boolean valid = false;
    for(final String validName : validNames) {
      if(address.endsWith("/" + validName)) {
        valid = true;
        break;
      }
    }
    return valid;
  }

  public void setValidName(final String name) {
    setValidNames(Arrays.asList(name));
  }

  public void setValidNames(final Iterable<String> names) {
    this.validNames = names;
  }
}

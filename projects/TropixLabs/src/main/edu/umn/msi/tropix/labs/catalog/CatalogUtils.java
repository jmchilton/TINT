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

package edu.umn.msi.tropix.labs.catalog;

import java.rmi.RemoteException;

import edu.mayo.bmi.bic.bobcat.api.CatalogEntryAPI;
import edu.umn.msi.tropix.common.logging.ExceptionUtils;

public class CatalogUtils {
  // TODO: Rename
  public static String getProvideIdFromServiceId(final CatalogEntryAPI entryService, final String serviceId) {
    try {
      return entryService.getFullDetails(new String[] {serviceId})[0].getProviderID();
    } catch(final RemoteException e) {
      throw ExceptionUtils.convertException(e);
    }
  }
}

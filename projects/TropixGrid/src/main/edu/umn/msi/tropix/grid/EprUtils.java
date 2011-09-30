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

package edu.umn.msi.tropix.grid;

import javax.annotation.Nonnull;

import org.apache.axis.message.addressing.Address;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI.MalformedURIException;

public class EprUtils {

  public static class MalformedURIRuntimeException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    MalformedURIRuntimeException(final MalformedURIException e) {
      super(e);
    }

  }

  public static EndpointReferenceType getEprForAddress(@Nonnull final String address) throws MalformedURIRuntimeException {
    final EndpointReferenceType epr = new EndpointReferenceType();
    try {
      epr.setAddress(new Address(address));
    } catch(final MalformedURIException e) {
      throw new MalformedURIRuntimeException(e);
    }
    return epr;
  }

}

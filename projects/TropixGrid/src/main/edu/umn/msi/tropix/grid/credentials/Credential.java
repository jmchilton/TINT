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

package edu.umn.msi.tropix.grid.credentials;

import org.globus.gsi.GlobusCredential;

/**
 * Unit testing with GlobusCredentials is difficult because GlobusCredential is not an interface and so cannot be mocked, and does not have a constructor that would allow dummy credentials to be created and used for testing.
 * 
 * To ease testing, all code that passes credentials around should do so via instances of this interface.
 * 
 * @author John Chilton
 * 
 */
public interface Credential {

  /**
   * @return The wrapped credential.
   */
  GlobusCredential getGlobusCredential();

  /**
   * 
   * @return The grid identity corresponding to the wrapped credential.
   */
  String getIdentity();

  /**
   * 
   * @return A serialization of the wrapped credential as bytes.
   */
  byte[] toBytes();
  
  /**
   * 
   * @return A serialization of the wrapped credential as a string.
   */
  String toString();

  /**
   * @return The number of seconds remaining before this credential expires, or 0 if the credential is expired.
   * 
   */
  long getTimeLeft();

}

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

package edu.umn.msi.tropix.client.directory;

import java.io.Serializable;

public class GridUser implements Serializable {
  private static final long serialVersionUID = -7185309034234483904L;
  private String firstName, lastName, institution, emailAddress, gridId;

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(final String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(final String lastName) {
    this.lastName = lastName;
  }

  public String getInstitution() {
    return institution;
  }

  public void setInstitution(final String institution) {
    this.institution = institution;
  }

  public String getEmailAddress() {
    return emailAddress;
  }

  public void setEmailAddress(final String emailAddress) {
    this.emailAddress = emailAddress;
  }

  public String getGridId() {
    return gridId;
  }

  public void setGridId(final String gridId) {
    this.gridId = gridId;
  }

  @Override
  public String toString() {
    String str;
    if(lastName != null) {
      if(firstName != null) {
        str = lastName + ", " + firstName;
      } else {
        str = lastName;
      }
      str = str + " (" + getUsername() + ")";
    } else {
      str = getUsername();
    }
    return str;
  }

  private String getUsername() {
    final String username;
    if(gridId.contains("=")) {
      username = gridId.substring(gridId.lastIndexOf('=') + 1);
    } else {
      username = gridId;
    }
    return username;
  }

}

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

package edu.umn.msi.tropix.webgui.services.object;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Permission implements Serializable {
  public enum Source {
    USER("Users"), GROUP("Groups"), SHARED_FOLDER("Shared Folders");
    private final String sectionHeader;

    private Source(final String sectionHeader) {
      this.sectionHeader = sectionHeader;
    }

    public String getSectionHeader() {
      return this.sectionHeader;
    }
  };

  public enum Type {
    READ("View"), WRITE("View and Edit"), FOLDER("View and Edit");
    private final String desc;

    private Type(final String desc) {
      this.desc = desc;
    }

    public String getDescription() {
      return this.desc;
    }
  };

  private String id;
  private String name;
  private Source source;
  private boolean immutable;
  private Type type;

  public String getId() {
    return this.id;
  }

  public void setId(final String id) {
    this.id = id;
  }

  public String getName() {
    return this.name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public Source getSource() {
    return this.source;
  }

  public void setSource(final Source source) {
    this.source = source;
  }

  public Type getType() {
    return this.type;
  }

  public void setType(final Type type) {
    this.type = type;
  }

  public boolean getImmutable() {
    return this.immutable;
  }

  public void setImmutable(final boolean immutable) {
    this.immutable = immutable;
  }
}

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

package edu.umn.msi.tropix.webgui.server.logging;

import org.apache.log4j.PatternLayout;
import org.apache.log4j.helpers.PatternConverter;
import org.apache.log4j.helpers.PatternParser;
import org.apache.log4j.spi.LoggingEvent;

import edu.umn.msi.tropix.webgui.server.security.UserSession;

public class TropixPatternLayout extends PatternLayout {
  private static UserSession userSession;

  /**
   * Bad practice for sure to set static field from an instance method, but its seems to be an easy way to share this userSession field between Log4J and Spring. TODO: A better approach would be to invoke a static method from Spring if possible. This would still be ugly to some
   * extent.
   */
  public void setUserSession(final UserSession userSession) {
    registerUserSession(userSession);
  }
  
  private static void registerUserSession(final UserSession userSession) {
    TropixPatternLayout.userSession = userSession;
  }
  

  public TropixPatternLayout() {
    super();
  }

  public TropixPatternLayout(final String pattern) {
    super(pattern);
  }

  @Override
  protected TropixPatternParser createPatternParser(final String pattern) {
    return new TropixPatternParser(pattern);
  }

  static class TropixPatternParser extends PatternParser {

    public TropixPatternParser(final String pattern) {
      super(pattern);
    }

    @Override
    protected void finalizeConverter(final char c) {
      if(c == 'G') {
        this.addConverter(new GridIdPatternConverter());
      } else {
        super.finalizeConverter(c);
      }
    }
  }

  static class GridIdPatternConverter extends PatternConverter {

    public String convert(final LoggingEvent arg0) {
      String gridId = "";
      if(userSession != null) {
        try {
          gridId = userSession.getGridId();
          gridId = gridId == null ? "" : gridId.substring(gridId.lastIndexOf("=") + 1);
        } catch(final Exception e) {
          gridId = "";
        }
      }
      return gridId;
    }
  }
}

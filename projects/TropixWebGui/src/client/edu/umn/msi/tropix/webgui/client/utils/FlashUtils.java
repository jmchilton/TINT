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

package edu.umn.msi.tropix.webgui.client.utils;

/**
 * This class contains utilities for interactive with the 
 * Flash plugin.
 * 
 * @author John Chilton
 *
 */
public class FlashUtils {

  /**
   * 
   * @return The major version of flash the user has such as
   * 9 or 10. This returns -1 if flash is not found.
   */
  public static int flashMajorVersion() {
    final String flashVersion = flashVersion();
    int majorVersion;
    if(flashVersion.equals("-1")) {
      majorVersion = -1;
    } else if(flashVersion.contains(" ")) { // XXX X,X,X,X
      majorVersion = Integer.parseInt(flashVersion.split(" ")[1].split(",")[0]);
    } else {
      majorVersion = Integer.parseInt(flashVersion.split("\\.")[0]);
    }
    return majorVersion;
  }
  
  /**
   * 
   * @return Flash version as a String.
   */
  public static native String flashVersion() /*-{
    // Flash Player Version Detection - Rev 1.6
    // Detect Client Browser type
    // Copyright(c) 2005-2006 Adobe Macromedia Software, LLC. All rights reserved.

    var isIE  = (navigator.appVersion.indexOf("MSIE") != -1) ? true : false;
    var isWin = (navigator.appVersion.toLowerCase().indexOf("win") != -1) ? true : false;
    var isOpera = (navigator.userAgent.indexOf("Opera") != -1) ? true : false;
   
    // NS/Opera version >= 3 check for Flash plugin in plugin array
    var flashVer = -1;

    if (navigator.plugins != null && navigator.plugins.length > 0) {
      if (navigator.plugins["Shockwave Flash 2.0"] || navigator.plugins["Shockwave Flash"]) {
        var swVer2 = navigator.plugins["Shockwave Flash 2.0"] ? " 2.0" : "";
        var flashDescription = navigator.plugins["Shockwave Flash" + swVer2].description;
        var descArray = flashDescription.split(" ");
        var tempArrayMajor = descArray[2].split(".");     
        var versionMajor = tempArrayMajor[0];
        var versionMinor = tempArrayMajor[1];
        var versionRevision = descArray[3];
        if (versionRevision == "") {
          versionRevision = descArray[4];
        }
        if (versionRevision[0] == "d") {
          versionRevision = versionRevision.substring(1);
        } else if (versionRevision[0] == "r") {
          versionRevision = versionRevision.substring(1);
          if (versionRevision.indexOf("d") > 0) {
            versionRevision = versionRevision.substring(0, versionRevision.indexOf("d"));
          }
        }
        var flashVer = versionMajor + "." + versionMinor + "." + versionRevision;
      }
    }
    // MSN/WebTV 2.6 supports Flash 4
    else if (navigator.userAgent.toLowerCase().indexOf("webtv/2.6") != -1) flashVer = 4;
    // WebTV 2.5 supports Flash 3
    else if (navigator.userAgent.toLowerCase().indexOf("webtv/2.5") != -1) flashVer = 3;
    // older WebTV supports Flash 2
    else if (navigator.userAgent.toLowerCase().indexOf("webtv") != -1) flashVer = 2;
    else if ( isIE && isWin && !isOpera ) {
      
      // flashVer = ControlVersion();
      var version;
      var axo;
      var e;
      // NOTE : new ActiveXObject(strFoo) throws an exception if strFoo isn't in the registry
      try {
        // version will be set for 7.X or greater players
        axo = new ActiveXObject("ShockwaveFlash.ShockwaveFlash.7");
        version = axo.GetVariable("$version");
      } catch (e) {
      }
      
      if (!version)
      {
        try {
          // version will be set for 6.X players only
          axo = new ActiveXObject("ShockwaveFlash.ShockwaveFlash.6");

          // installed player is some revision of 6.0
          // GetVariable("$version") crashes for versions 6.0.22 through 6.0.29,
          // so we have to be careful. 

          // default to the first public version
          version = "WIN 6,0,21,0";

          // throws if AllowScripAccess does not exist (introduced in 6.0r47)   
          axo.AllowScriptAccess = "always";

          // safe to call for 6.0r47 or greater
          version = axo.GetVariable("$version");

        } catch (e) {
        }
      }

      if (!version)
      {
        try {
          // version will be set for 4.X or 5.X player
          axo = new ActiveXObject("ShockwaveFlash.ShockwaveFlash.3");
          version = axo.GetVariable("$version");
        } catch (e) {
        }
      }

      if (!version)
      {
        try {
          // version will be set for 3.X player
          axo = new ActiveXObject("ShockwaveFlash.ShockwaveFlash.3");
          version = "WIN 3,0,18,0";
        } catch (e) {
        }
      }

      if (!version)
      {
        try {
          // version will be set for 2.X player
          axo = new ActiveXObject("ShockwaveFlash.ShockwaveFlash");
          version = "WIN 2,0,0,11";
        } catch (e) {
          version = -1;
        }
      }
      flashVer = version; //ControlVersion();
    } 
    return flashVer;
  }-*/;


}

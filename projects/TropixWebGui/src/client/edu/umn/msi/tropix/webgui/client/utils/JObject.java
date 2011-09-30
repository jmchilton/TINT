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

// TODO: Implement putting.

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

public interface JObject {
  String getString(String key);

  String getStringOrNull(String key);

  JObject getJObject(String key);

  JObject getJObjectOrNull(String key);

  JArray getJArray(String key);

  JArray getJArrayOrNull(String key);

  boolean getBoolean(String key);

  int getInt(String key);

  double getDouble(String key);

  boolean containsKey(String key);

  java.util.Set<String> keySet();

  int size();

  String toString();

  public static class Factory {
    public static JObject create(final String string) {
      return Factory.create(JSONParser.parse(string));
    }

    public static JObject create(final JSONValue value) {
      return Factory.create(value.isObject());
    }

    public static JObject create(final JSONObject object) {
      return new JObjectImpl(object);
    }
  }
}

abstract class AbstractJObjectImpl implements JObject {
  public abstract String getString(String key);

  public abstract String getStringOrNull(String key);

  public abstract JObject getJObject(String key);

  public abstract JObject getJObjectOrNull(String key);

  public abstract JArray getJArray(String key);

  public abstract JArray getJArrayOrNull(String key);

  private JSONObject object;

  AbstractJObjectImpl(final JSONObject object) {
    this.setObject(object);
  }

  public boolean containsKey(final String key) {
    return this.getObject().containsKey(key);
  }

  public java.util.Set<String> keySet() {
    return this.getObject().keySet();
  }

  public int size() {
    return this.getObject().size();
  }

  public String toString() {
    return this.getObject().toString();
  }

  void setObject(final JSONObject object) {
    this.object = object;
  }

  JSONObject getObject() {
    return object;
  }
}

class JObjectImpl extends AbstractJObjectImpl {
  JObjectImpl(final JSONObject object) {
    super(object);
  }

  public String getString(final String key) {
    return this.getObject().get(key).isString().stringValue();
  }

  public String getStringOrNull(final String key) {
    final JSONValue value = this.getObject().get(key);
    if(value == null) {
      return null;
    }
    final JSONString jsonString = value.isString();
    if(value == null) {
      return null;
    }
    return jsonString.stringValue();
  }

  public JObject getJObject(final String key) {
    return new JObjectImpl(this.getObject().get(key).isObject());
  }

  public JObject getJObjectOrNull(final String key) {
    final JSONValue value = this.getObject().get(key);
    if(value == null) {
      return null;
    }
    return new JObjectImpl(value.isObject());
  }

  public JArray getJArray(final String key) {
    return new JArrayImpl(JArray.Utils.toJSONArray(this.getObject().get(key)));
  }

  public JArray getJArrayOrNull(final String key) {
    final JSONValue value = this.getObject().get(key);
    if(value == null) {
      return null;
    }
    final JSONArray array = JArray.Utils.toJSONArray(value);
    if(array == null) {
      return null;
    }
    return new JArrayImpl(array);
  }

  public boolean getBoolean(final String key) {
    return this.getObject().get(key).isBoolean().booleanValue();
  }

  public int getInt(final String key) {
    return Integer.parseInt(this.getObject().get(key).isString().stringValue());
  }

  public double getDouble(final String key) {
    return this.getObject().get(key).isNumber().doubleValue();
  }

}

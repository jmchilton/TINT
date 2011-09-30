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

import java.util.Iterator;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

public interface JArray {
  String getString(int key);

  JObject getJObject(int key);

  JArray getJArray(int key);

  boolean getBoolean(int key);

  int getInt(int key);

  double getDouble(int key);

  int size();

  String toString();

  Iterator<String> stringIterator();

  Iterator<Integer> integerIterator();

  Iterator<Double> doubleIterator();

  Iterator<JArray> jArrayIterator();

  Iterator<JObject> jObjectIterator();

  Iterable<JObject> asJObjectIterable();

  public static class Factory {
    public static JArray create(final String string) {
      return Factory.create(JSONParser.parse(string));
    }

    public static JArray create(final JSONValue value) {
      return Factory.create(value.isArray());
    }

    public static JArray create(final JSONArray array) {
      return new JArrayImpl(array);
    }
  }

  public static class Utils {
    public static JSONArray toJSONArray(final JSONValue value) {
      if(value.isArray() != null) {
        return value.isArray();
      } else {
        final JSONArray singleton = new JSONArray();
        singleton.set(0, value);
        return singleton;
      }
    }
  }

}

abstract class AbstractJArrayImpl implements JArray {
  public abstract String getString(int index);

  public abstract JObject getJObject(int index);

  public abstract JArray getJArray(int index);

  public abstract boolean getBoolean(int key);

  public abstract int getInt(int key);

  public abstract double getDouble(int key);

  private JSONArray array;

  AbstractJArrayImpl(final JSONArray array) {
    this.setArray(array);
  }

  public int size() {
    return this.getArray().size();
  }

  public String toString() {
    return this.getArray().toString();
  }

  public Iterator<String> stringIterator() {
    return new StringIterator(this);
  }

  public Iterator<Integer> integerIterator() {
    return new IntegerIterator(this);
  }

  public Iterator<Double> doubleIterator() {
    return new DoubleIterator(this);
  }

  public Iterator<JArray> jArrayIterator() {
    return new JArrayIterator(this);
  }

  public Iterator<JObject> jObjectIterator() {
    return new JObjectIterator(this);
  }

  void setArray(final JSONArray array) {
    this.array = array;
  }

  JSONArray getArray() {
    return array;
  }

  private abstract class AbstractIterator<T> implements Iterator<T> {
    private final JArray array;
    private int curIndex;
    private int size;

    AbstractIterator(final JArray array) {
      this.array = array;
      this.setSize(array.size());
      this.curIndex = 0;
    }

    protected int nextIndex() {
      return this.curIndex++;
    }

    public boolean hasNext() {
      return this.curIndex < this.getSize();
    }

    public void remove() {
      throw new UnsupportedOperationException();
    }

    public abstract T next();

    JArray getArray() {
      return array;
    }

    void setSize(final int size) {
      this.size = size;
    }

    int getSize() {
      return size;
    }
  }

  class StringIterator extends AbstractIterator<String> {
    StringIterator(final JArray array) {
      super(array);
    }

    public String next() {
      return this.getArray().getString(this.nextIndex());
    }
  }

  class IntegerIterator extends AbstractIterator<Integer> {
    IntegerIterator(final JArray array) {
      super(array);
    }

    public Integer next() {
      return new Integer(this.getArray().getInt(this.nextIndex()));
    }
  }

  class DoubleIterator extends AbstractIterator<Double> {
    DoubleIterator(final JArray array) {
      super(array);
    }

    public Double next() {
      return new Double(this.getArray().getDouble(this.nextIndex()));
    }
  }

  class JObjectIterator extends AbstractIterator<JObject> {
    JObjectIterator(final JArray array) {
      super(array);
    }

    public JObject next() {
      return this.getArray().getJObject(this.nextIndex());
    }
  }

  class JArrayIterator extends AbstractIterator<JArray> {
    JArrayIterator(final JArray array) {
      super(array);
    }

    public JArray next() {
      return this.getArray().getJArray(this.nextIndex());
    }
  }

}

class JArrayImpl extends AbstractJArrayImpl {
  JArrayImpl(final JSONArray array) {
    super(array);
  }

  public String getString(final int index) {
    return this.getArray().get(index).isString().stringValue();
  }

  public JObject getJObject(final int index) {
    return new JObjectImpl(this.getArray().get(index).isObject());
  }

  public JArray getJArray(final int index) {
    return new JArrayImpl(JArray.Utils.toJSONArray(this.getArray().get(index)));
  }

  public boolean getBoolean(final int index) {
    return this.getArray().get(index).isBoolean().booleanValue();
  }

  public int getInt(final int index) {
    return Integer.parseInt(this.getArray().get(index).isString().stringValue());
  }

  public double getDouble(final int index) {
    return this.getArray().get(index).isNumber().doubleValue();
  }

  public Iterable<JObject> asJObjectIterable() {
    return new Iterable<JObject>() {
      public Iterator<JObject> iterator() {
        return jObjectIterator();
      }
    };
  }
}

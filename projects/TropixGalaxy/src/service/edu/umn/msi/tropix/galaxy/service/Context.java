package edu.umn.msi.tropix.galaxy.service;

import java.util.Map;

import org.python.core.Py;
import org.python.core.PyDictionary;
import org.python.core.PyObject;
import org.python.core.PyString;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

public class Context {
  private Map<String, Object> backingMap = Maps.newHashMap();
  private String value;

  public Context() {
  }
  
  public Context(final String value) {
    this.value = value;
  }
    
  public Object get(final String key) {
    return backingMap.get(key);
  }
  
  public String toString() {
    return value;
  }
  
  public Context(final String value, final Map<String, Object> backingMap) {
    this(value);
    this.backingMap.putAll(backingMap);
  }

  public Context(final Map<String, Object> backingMap) {
    this.backingMap.putAll(backingMap);
  }

  public void put(final String key, final Object value) {
    backingMap.put(key, value);
  }
  
  public PyObject asPyObject() {
    final PyDictionary dictionary = backingMapAsPyDictionary();
    final PyObject pyName = value == null ? Py.None : asPyString(value);
    final PyObject namedOdictClass = JythonEnv.getClass("named_odict");
    final PyObject namedOdict = namedOdictClass.__call__(new PyObject[] {pyName, dictionary});
    return namedOdict;
  }

  private PyDictionary backingMapAsPyDictionary() {
    final Map<PyObject, PyObject> pythonObjectMap = Maps.newHashMap();
    for(final Map.Entry<String, Object> entry : backingMap.entrySet()) {
      final PyString pyKey = asPyString(entry.getKey());
      final Object value = entry.getValue();
      final PyObject pyValue = getAsPyObject(value);
      pythonObjectMap.put(pyKey, pyValue);
    }
    final PyDictionary dictionary = new PyDictionary(pythonObjectMap);
    return dictionary;
  }
  
  private static PyObject getAsPyObject(final Object value) {
    PyObject pyObject = null;
    if(value instanceof String) {
      pyObject = asPyString(value);
    } else if(value instanceof Context) {
      pyObject = ((Context) value).asPyObject();
    }
    Preconditions.checkState(pyObject != null, String.format("Failed to convert object %s to python.", value.toString()));
    return pyObject;
  }
  
  private static PyString asPyString(final Object object) {
    Preconditions.checkArgument(object instanceof String);
    return new PyString((String) object);
  }

}

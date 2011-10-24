package edu.umn.msi.tropix.galaxy.service;

import java.util.Map;

import org.python.core.Py;
import org.python.core.PyDictionary;
import org.python.core.PyList;
import org.python.core.PyObject;
import org.python.core.PyString;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

public class Context {
  private Map<String, Object> backingMap = Maps.newLinkedHashMap();
  private String value;
  
  public Map<String, Object> getBackingMap() {
    return backingMap;
  }

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
    } else if(value instanceof Iterable) {
      final PyList list = new PyList();
      @SuppressWarnings("unchecked")
      final Iterable<Object> valueIterable = (Iterable<Object>) value;
      for(final Object child : valueIterable) {
        list.add(getAsPyObject(child));        
      }
      pyObject = list;
    }
    Preconditions.checkState(pyObject != null, String.format("Failed to convert object %s of class %s to python.", value.toString(), value.getClass().getName()));
    return pyObject;
  }
  
  private static PyString asPyString(final Object object) {
    Preconditions.checkArgument(object instanceof String);
    return new PyString((String) object);
  }
  
}

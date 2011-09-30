/********************************************************************************
 * Copyright (c) 2009 Regents of the University of Minnesota
 *
 * This Software was written at the Minnesota Supercomputing Institute
 * http://msi.umn.edu
 *
 * All rights reserved. The following statement of license applies
 * only to this file, and and not to the other files distributed with it
 * or derived therefrom.  This file is made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Minnesota Supercomputing Institute - initial API and implementation
 *******************************************************************************/

package edu.umn.msi.tropix.proteomics.parameters;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.collect.Maps;

import edu.umn.msi.tropix.common.io.IORuntimeException;

public class ParameterUtils {
  private static Log logger = LogFactory.getLog(ParameterUtils.class);

  /**
   * Reads properties input a map and calls {@link #setParametersFromMap(Map, Object)}.
   * 
   * @param inputStream
   *          Stream to read Java properties from.
   * @param parameterObject
   *          Object to set parameters on. See {@link #setParametersFromMap(Map, Object)}.
   * @throws IOException
   */
  public static void setParametersFromProperties(final InputStream inputStream, final Object parameterObject) {
    final Properties props = new Properties();
    try {
      props.load(inputStream);
    } catch(final IOException e) {
      throw new IORuntimeException(e);
    }
    setParametersFromMap(props, parameterObject);
  }

  /**
   * Uses reflection to dynamically set a parameter bean's parameters from a given map of Strings.
   * 
   * @param parameterMap
   *          Mapping of parameter names (attributes) to values.
   * @param parameterObject
   *          Java bean to set parameter values of of. Setter methods must conform to the Java bean naming scheme and must take in one argument of
   *          type String, Integer, Long, Float, or Double.
   * @exception IllegalArgumentException
   *              Thrown if a given setter method cannot be found or if the setter method does not conform to the rules described.
   */
  @SuppressWarnings("unchecked")
  public static void setParametersFromMap(final Map parameterMap, final Object parameterObject) {
    final Method[] methods = parameterObject.getClass().getMethods();
    final Map<String, Method> objectSetMethodMap = new HashMap<String, Method>(methods.length);

    for(final Method method : parameterObject.getClass().getMethods()) {
      final String methodName = method.getName();
      if(methodName.contains("set")) {
        objectSetMethodMap.put(method.getName(), method);
      }
    }

    for(final Object keyObject : parameterMap.keySet()) {
      final String key = (String) keyObject;
      final String value = (String) parameterMap.get(key);
      final String setterMethodName = "set" + key.substring(0, 1).toUpperCase() + key.substring(1);
      final Method setterMethod = objectSetMethodMap.get(setterMethodName);
      if(setterMethod == null) {
        continue;
      }
      final Class<?>[] parameterTypes = setterMethod.getParameterTypes();
      if(parameterTypes.length != 1) {
        throw new IllegalArgumentException("Illegal setter method found, must take in exactly one argument.");
      }
      final Class<?> argumentType = parameterTypes[0];
      try {
        if(argumentType.equals(String.class)) {
          setterMethod.invoke(parameterObject, value);
          continue;
        }
        if(value == null || value.trim().equals("")) {
          setterMethod.invoke(parameterObject, (Object) null);
          continue;
        }
        if(argumentType.equals(Double.class) || argumentType.equals(double.class)) {
          setterMethod.invoke(parameterObject, Double.parseDouble(value));
        } else if(argumentType.equals(Integer.class) || argumentType.equals(int.class)) {
          setterMethod.invoke(parameterObject, Integer.parseInt(value));
        } else if(argumentType.equals(Long.class) || argumentType.equals(long.class)) {
          setterMethod.invoke(parameterObject, Long.parseLong(value));
        } else if(argumentType.equals(Float.class) || argumentType.equals(float.class)) {
          setterMethod.invoke(parameterObject, Float.parseFloat(value));
        } else if(argumentType.equals(Boolean.class) || argumentType.equals(boolean.class)) {
          setterMethod.invoke(parameterObject, Boolean.parseBoolean(value));
        } else if(argumentType.equals(Short.class) || argumentType.equals(short.class)) {
          setterMethod.invoke(parameterObject, Short.parseShort(value));
        } else {
          throw new IllegalArgumentException("Illegal type found for argument to setter bean - type is " + argumentType + " - key is " + key);
        }
      } catch(final Exception e) {
        throw new IllegalArgumentException(e);
      }
    }
  }

  /**
   * This is the (mostly) inverse of {@link #setParametersFromMap(Map, Object)}. Given a parameter object that is bean where the parameters are the
   * attributes with a simple type (i.e. Double, Integer, Float, or Boolean), a map of the attribute names to values (as strings) is
   * created.
   * 
   * @param parameters
   *          Parameter object to pull keys and values from.
   * @return
   */
  @SuppressWarnings("unchecked")
  public static void setMapFromParameters(final Object parameters, final Map parameterMap) {
    final List<Class> simpleTypes = java.util.Arrays.asList(new Class[] {Double.class, Integer.class, Float.class, Boolean.class, Long.class,
        String.class, Short.class, double.class, int.class, float.class, boolean.class, long.class, short.class});
    for(final Method method : parameters.getClass().getMethods()) {
      String methodName = method.getName();
      if(method.getParameterTypes().length != 0 || !(methodName.startsWith("get") || methodName.startsWith("is"))
          || !simpleTypes.contains(method.getReturnType())) {
        continue;
      }
      String attributeName;
      if(methodName.startsWith("get")) {
        attributeName = methodName.substring(3, 4).toLowerCase() + methodName.substring(4);
      } else { // is method
        attributeName = methodName.substring(2, 3).toLowerCase() + methodName.substring(3);
      }
      Object result;
      try {
        result = method.invoke(parameters);
      } catch(final Exception e) {
        logger.info(e);
        throw new IllegalArgumentException("Failed to invoke get method on bean, should not happen with simple beans.", e);
      }
      if(result != null) {
        parameterMap.put(attributeName, result.toString());
      } // else null value found in setMapFromParameters, not adding it to the map"
    }
  }

  public static Map<String, String> mapForParameters(final Object parameters) {
    final Map<String, String> parameterMap = Maps.newHashMap();
    setMapFromParameters(parameters, parameterMap);
    return parameterMap;
  }
}

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

package edu.umn.msi.tropix.proteomics.test;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.io.IORuntimeException;
import edu.umn.msi.tropix.models.sequest.SequestParameters;
import edu.umn.msi.tropix.proteomics.parameters.ParameterUtils;

public class ParameterUtilsTest {
  interface TestBean {
    void setDouble(Double x);

    Double getDouble();

    void setFloat(Float x);

    Float getFloat();

    void setInteger(Integer x);

    Integer getInteger();

    void setLong(Long x);

    Long getLong();

    void setString(String x);

    String getString();

    void setObject(Object obj);

    Object getObject();

    void setStrings(String a1, String a2);
  }

  class InvokeTestBean {
    public void setMoo(final Double x) {
      throw new RuntimeException("moo");
    }
  }

  @Test(groups = "unit", expectedExceptions = IORuntimeException.class)
  public void loadProblem() {
    ParameterUtils.setParametersFromProperties(new InputStream() {
      public int read() throws IOException {
        throw new IOException();
      }
    }, new SequestParameters());
  }

  @Test(groups = "unit", expectedExceptions = RuntimeException.class)
  public void invoke() {
    final Map<String, String> map = new HashMap<String, String>();
    map.put("moo", "1.2");
    ParameterUtils.setParametersFromMap(map, new InvokeTestBean());
  }

  class NotNullMoo {
    private Integer moo = 1;

    public void setMoo(final Integer moo) {
      this.moo = moo;
    }
  }

  @Test(groups = "unit", expectedExceptions = RuntimeException.class)
  public void nullMapValue() {
    final Map<String, String> map = new HashMap<String, String>();
    map.put("moo", null);
    final NotNullMoo moo = new NotNullMoo();
    ParameterUtils.setParametersFromMap(map, moo);
    assert moo.moo == null;

  }

  @Test(groups = "unit", expectedExceptions = RuntimeException.class)
  public void paramToMapInvokationException() {
    @SuppressWarnings("hiding")
    class Moo {
      @SuppressWarnings("unused")
      public Integer getFoo() {
        throw new IllegalStateException();
      }
    }
    final Map<String, String> map = new HashMap<String, String>();
    ParameterUtils.setMapFromParameters(new Moo(), map);
  }

  public static class Moo {
    private String foo = null;

    public void setFoo(final String foo) {
      this.foo = foo;
    }
  }

  @Test(groups = "unit")
  public void paramToMapExtraParam() {
    final Map<String, String> map = new HashMap<String, String>();
    map.put("foo", "fooval");
    // map.put("foo2", null);
    map.put("bar", "barval");
    final Moo moo = new Moo();
    ParameterUtils.setParametersFromMap(map, moo);
    assert moo.foo.equals("fooval");
  }

  @Test(groups = "unit", expectedExceptions = RuntimeException.class)
  public void paramToMapNull() {
    @SuppressWarnings("hiding")
    class Moo {
      @SuppressWarnings("unused")
      public Integer getFoo() {
        return null;
      }
    }
    final Map<String, String> map = new HashMap<String, String>();
    ParameterUtils.setMapFromParameters(new Moo(), map);
    assert !map.containsKey("foo");
  }

  @Test(groups = {"parameter.utils", "unit"})
  public void testSetMapFromParameters() throws IOException {
    final TestBean mockBean = createMock(TestBean.class);
    expect(mockBean.getDouble()).andReturn(42d);
    expect(mockBean.getFloat()).andReturn(43f);
    expect(mockBean.getInteger()).andReturn(44);
    expect(mockBean.getLong()).andReturn(45L);
    expect(mockBean.getString()).andReturn("Fourty-six");
    replay(mockBean);
    final Map<String, String> map = new HashMap<String, String>();
    ParameterUtils.setMapFromParameters(mockBean, map);
    verify(mockBean);
    assert map.containsKey("double");
    assert map.get("double").toString().startsWith("42");
    assert map.get("float").toString().startsWith("43");
    assert map.get("integer").equals("44");
    assert map.get("long").equals("45");
    assert map.get("string").equals("Fourty-six");
  }

  @Test(groups = {"parameter.utils", "unit"})
  public void testSetParametersFromMap() throws IOException {
    final Map<String, String> testMap = new HashMap<String, String>();
    final TestBean mockBean = createMock(TestBean.class);
    mockBean.setDouble(4.5);
    mockBean.setFloat(1.2f);
    mockBean.setInteger(1);
    mockBean.setString("foobar");
    mockBean.setLong(2L);
    replay(mockBean);
    testMap.put("double", "4.5");
    testMap.put("float", "1.2");
    testMap.put("integer", "1");
    testMap.put("string", "foobar");
    testMap.put("long", "2");
    ParameterUtils.setParametersFromMap(testMap, mockBean);
    verify(mockBean);
  }

  @Test(groups = {"parameter.utils", "unit"})
  public void testSetParametersFromProperties() throws IOException {
    final TestBean mockBean = createMock(TestBean.class);
    mockBean.setDouble(4.5);
    mockBean.setFloat(1.2f);
    mockBean.setInteger(1);
    mockBean.setString("foobar");
    mockBean.setLong(2L);
    replay(mockBean);
    final String properties = "double=4.5\nfloat=1.2\ninteger=1\nstring=foobar\nlong=2";
    ParameterUtils.setParametersFromProperties(new ByteArrayInputStream(properties.getBytes()), mockBean);
    verify(mockBean);
  }

  @Test(groups = {"parameter.utils", "unit"}, expectedExceptions = {IllegalArgumentException.class})
  public void testInvalidArgumentObject() {
    final Map<String, String> testMap = new HashMap<String, String>();
    final TestBean mockBean = createMock(TestBean.class);
    testMap.put("Object", "8");
    ParameterUtils.setParametersFromMap(testMap, mockBean);
    assert false : "Exception not thrown";
  }

  @Test(groups = {"parameter.utils", "unit"}, expectedExceptions = {IllegalArgumentException.class})
  public void testInvalidArgumentMultipleArg() {
    final Map<String, String> testMap = new HashMap<String, String>();
    final TestBean mockBean = createMock(TestBean.class);
    testMap.put("strings", "hello");
    ParameterUtils.setParametersFromMap(testMap, mockBean);
    assert false : "Exception not thrown";
  }

}

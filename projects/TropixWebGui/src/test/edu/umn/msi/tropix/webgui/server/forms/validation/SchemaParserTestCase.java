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

package edu.umn.msi.tropix.webgui.server.forms.validation;

import java.io.InputStream;
import java.util.Map;

import org.testng.annotations.Test;

import junit.framework.Assert;

import com.google.common.base.Predicate;

public class SchemaParserTestCase {
  
  @Test(groups = "unit")
  public void testParse1() throws Exception {
    final InputStream schemaStream = this.getClass().getResourceAsStream("test1.xsd");
    final SchemaParserImpl parser = new SchemaParserImpl();
    final Map<String, Predicate<String>> predicates = parser.parse(schemaStream);
    Assert.assertTrue(predicates.get("aString").apply("hello"));
    Assert.assertTrue(predicates.get("aDouble").apply("1.3"));
    Assert.assertFalse(predicates.get("aDouble").apply("hello"));
    Assert.assertTrue(predicates.get("aInt").apply("1"));
    Assert.assertFalse(predicates.get("aInt").apply("1.4"));
  }

  @Test(groups = "unit")
  public void testParse2() throws Exception {
    final InputStream schemaStream = this.getClass().getResourceAsStream("test2.xsd");
    final SchemaParserImpl parser = new SchemaParserImpl();
    final Map<String, Predicate<String>> predicates = parser.parse(schemaStream);
    Assert.assertTrue(predicates.get("theName").apply("23242"));
    Assert.assertFalse(predicates.get("theName").apply("hello"));
  }

  @Test(groups = "unit")
  public void testParse3() throws Exception {
    final InputStream schemaStream = this.getClass().getResourceAsStream("test3.xsd");
    final SchemaParserImpl parser = new SchemaParserImpl();
    final Map<String, Predicate<String>> predicates = parser.parse(schemaStream);
    Assert.assertTrue(predicates.get("theName").apply("23242"));
    Assert.assertFalse(predicates.get("theName").apply("hello"));
  }

  @Test(groups = "unit")
  public void testParse4() throws Exception {
    final InputStream schemaStream = this.getClass().getResourceAsStream("test4.xsd");
    final SchemaParserImpl parser = new SchemaParserImpl();
    final Map<String, Predicate<String>> predicates = parser.parse(schemaStream);
    Assert.assertFalse(predicates.get("theName").apply("23242"));
    Assert.assertFalse(predicates.get("theName").apply("hello"));
    Assert.assertTrue(predicates.get("theName").apply("0"));
    Assert.assertTrue(predicates.get("theName").apply("1"));
    Assert.assertTrue(predicates.get("theName").apply("2"));
  }

  @Test(groups = "unit")
  public void testParse5() throws Exception {
    final InputStream schemaStream = this.getClass().getResourceAsStream("test5.xsd");
    final SchemaParserImpl parser = new SchemaParserImpl();
    final Map<String, Predicate<String>> predicates = parser.parse(schemaStream);
    Assert.assertTrue(predicates.get("theInt").apply("0"));
    Assert.assertFalse(predicates.get("theInt").apply("-1"));
    Assert.assertTrue(predicates.get("theInt").apply("1"));
    Assert.assertTrue(predicates.get("theInt").apply("100"));
    Assert.assertFalse(predicates.get("theInt").apply("101"));
    Assert.assertTrue(predicates.get("theInt").apply("99"));

    Assert.assertTrue(predicates.get("theDouble").apply("1.2"));
    Assert.assertTrue(predicates.get("theDouble").apply("0"));
    Assert.assertFalse(predicates.get("theDouble").apply("-0.01"));
    Assert.assertTrue(predicates.get("theDouble").apply("100.0"));
    Assert.assertTrue(predicates.get("theDouble").apply("99.0"));
    Assert.assertFalse(predicates.get("theDouble").apply("100.03"));
  }

  @Test(groups = "unit")
  public void testParse6() throws Exception {
    final InputStream schemaStream = this.getClass().getResourceAsStream("test6.xsd");
    final SchemaParserImpl parser = new SchemaParserImpl();
    final Map<String, Predicate<String>> predicates = parser.parse(schemaStream);
    Assert.assertFalse(predicates.get("theInt").apply("0"));
    Assert.assertFalse(predicates.get("theInt").apply("-1"));
    Assert.assertTrue(predicates.get("theInt").apply("1"));
    Assert.assertFalse(predicates.get("theInt").apply("100"));
    Assert.assertFalse(predicates.get("theInt").apply("101"));
    Assert.assertTrue(predicates.get("theInt").apply("99"));

    Assert.assertTrue(predicates.get("theDouble").apply("1.2"));
    Assert.assertFalse(predicates.get("theDouble").apply("0"));
    Assert.assertFalse(predicates.get("theDouble").apply("-0.01"));
    Assert.assertFalse(predicates.get("theDouble").apply("100.0"));
    Assert.assertTrue(predicates.get("theDouble").apply("99.0"));
    Assert.assertFalse(predicates.get("theDouble").apply("100.03"));
  }

  @Test(groups = "unit")
  public void testParse7() throws Exception {
    final InputStream schemaStream = this.getClass().getResourceAsStream("test7.xsd");
    final SchemaParserImpl parser = new SchemaParserImpl();
    final Map<String, Predicate<String>> predicates = parser.parse(schemaStream);
    Assert.assertTrue(predicates.get("theName").apply("23242"));
    Assert.assertFalse(predicates.get("theName").apply("hello"));
  }

  @Test(groups = "unit")
  public void testParse8() throws Exception {
    final InputStream schemaStream = this.getClass().getResourceAsStream("test8.xsd");
    final SchemaParserImpl parser = new SchemaParserImpl();
    final Map<String, Predicate<String>> predicates = parser.parse(schemaStream);
    Assert.assertTrue(predicates.get("theName").apply(" hello world "));
    Assert.assertFalse(predicates.get("theName").apply("hello world"));
    Assert.assertFalse(predicates.get("theName").apply(" helloworld "));
    Assert.assertFalse(predicates.get("theName").apply("helloworld "));
  }

  @Test(groups = "unit")
  public void testParse9() throws Exception {
    final InputStream schemaStream = this.getClass().getResourceAsStream("test9.xsd");
    final SchemaParserImpl parser = new SchemaParserImpl();
    final Map<String, Predicate<String>> predicates = parser.parse(schemaStream);

    Assert.assertTrue(predicates.get("theName").apply("101"));
    Assert.assertFalse(predicates.get("theName").apply("99"));
    Assert.assertTrue(predicates.get("theName").apply("4"));
    Assert.assertFalse(predicates.get("theName").apply("6"));
  }

  @Test(groups = "unit")
  public void testParse10() throws Exception {
    final InputStream schemaStream = this.getClass().getResourceAsStream("test10.xsd");
    final SchemaParserImpl parser = new SchemaParserImpl();
    final Map<String, Predicate<String>> predicates = parser.parse(schemaStream);

    Assert.assertTrue(predicates.get("theName").apply("101"));
    Assert.assertFalse(predicates.get("theName").apply("99"));
    Assert.assertTrue(predicates.get("theName").apply("4"));
    Assert.assertFalse(predicates.get("theName").apply("6"));
  }

  @Test(groups = "unit")
  public void testParse11() throws Exception {
    final InputStream schemaStream = this.getClass().getResourceAsStream("test11.xsd");
    final SchemaParserImpl parser = new SchemaParserImpl();
    final Map<String, Predicate<String>> predicates = parser.parse(schemaStream);

    Assert.assertTrue(predicates.get("intList").apply("101 1231 142   1  "));
    Assert.assertTrue(predicates.get("intList").apply("101"));
    Assert.assertTrue(predicates.get("intList").apply(""));

    Assert.assertFalse(predicates.get("intList").apply("101 1231 142   1 sadfasdfsad "));
    Assert.assertFalse(predicates.get("intList").apply("101 101.3"));
    Assert.assertFalse(predicates.get("intList").apply("adfasdf"));

    Assert.assertTrue(predicates.get("doubleList").apply("101 1231.2 142   1  "));
    Assert.assertTrue(predicates.get("doubleList").apply("101.5"));
    Assert.assertTrue(predicates.get("doubleList").apply(""));

    Assert.assertFalse(predicates.get("doubleList").apply("101 1231 142   1 sadfasdfsad "));
    Assert.assertFalse(predicates.get("doubleList").apply("101 101.3 x"));
    Assert.assertFalse(predicates.get("doubleList").apply("adfasdf "));
  }

  @Test(groups = "unit")
  public void testParse12() throws Exception {
    final InputStream schemaStream = this.getClass().getResourceAsStream("test12.xsd");
    final SchemaParserImpl parser = new SchemaParserImpl();
    final Map<String, Predicate<String>> predicates = parser.parse(schemaStream);
    Assert.assertTrue(predicates.get("theName").apply("23242"));
    Assert.assertFalse(predicates.get("theName").apply("hello"));
  }

  /*
  public void testParse13() throws Exception {
    final InputStream schemaStream = this.getClass().getResourceAsStream("test13.xsd");
    final SchemaParserImpl parser = new SchemaParserImpl();
    final Map<String, Predicate<String>> predicates = parser.parse(schemaStream);
    Assert.assertTrue(predicates.get("theName").apply("23242"));
    Assert.assertTrue(predicates.get("theName").apply("1"));
    Assert.assertTrue(predicates.get("theName").apply("-23242"));
    Assert.assertFalse(predicates.get("theName").apply("hello"));
    Assert.assertFalse(predicates.get("theName").apply("1231.1231231"));
  }
  */

}

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

package edu.umn.msi.tropix.galaxy.xml;

import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.namespace.QName;

import com.google.common.base.Preconditions;

import edu.umn.msi.tropix.common.xml.XMLUtility;
import edu.umn.msi.tropix.galaxy.inputs.cagrid.RootInput;
import edu.umn.msi.tropix.galaxy.tool.cagrid.Tool;
import edu.umn.msi.tropix.grid.xml.SerializationUtils;
import edu.umn.msi.tropix.grid.xml.SerializationUtilsFactory;
import edu.umn.msi.tropix.grid.xml.XmlConversionUtils;
import gov.nih.nci.cagrid.common.Utils;

public class GalaxyXmlUtils {
  private static final SerializationUtils SERIALIZATION_UTILS = SerializationUtilsFactory.getInstance();

  private static final XMLUtility<edu.umn.msi.tropix.galaxy.tool.Tool> TOOL_XML_UTILITY = new XMLUtility<edu.umn.msi.tropix.galaxy.tool.Tool>(edu.umn.msi.tropix.galaxy.tool.Tool.class);
  private static final XMLUtility<edu.umn.msi.tropix.galaxy.inputs.RootInput> INPUT_XML_UTILITY = new XMLUtility<edu.umn.msi.tropix.galaxy.inputs.RootInput>(edu.umn.msi.tropix.galaxy.inputs.RootInput.class);
  private static final QName TOOL_QNAME = new QName("http://msi.umn.edu/tropix/galaxy", "tool");
  static {
    TOOL_XML_UTILITY.setNamespaceFilter(TOOL_QNAME.getNamespaceURI());
  }
  private static final QName ROOT_INPUT_QNAME = new QName("http://msi.umn.edu/tropix/galaxy/inputs", "rootInput");

  /*
   * The follow two methods replace ones in XmlConversionUtils and hack around a bug in Axis 1.2 that prevents group constructs from working properly.
   */
  @SuppressWarnings("unchecked")
  public static Object jaxbToCaGrid(final Object input, final XMLUtility wrapper, final Class c) {
    String inputXml = wrapper.toString(input);
    int beginTests = inputXml.indexOf("<test");
    String preTestXml, postTestXml;
    if(beginTests > 0) {
      preTestXml = inputXml.substring(0, beginTests);
      postTestXml = inputXml.substring(beginTests);
    } else {
      preTestXml = inputXml;
      postTestXml = "";
    }
    preTestXml = preTestXml.replaceAll("\\<param ([^/]*)/>", "<param $1></param>");
    preTestXml = preTestXml.replaceAll("\\<(param|conditional|repeat)", "<InputElement><$1")
                           .replaceAll("\\</(param|conditional|repeat)>", "</$1></InputElement>");

    final String hackedXml = preTestXml + postTestXml;
    final StringReader reader = new StringReader(hackedXml);
    try {
      return Utils.deserializeObject(reader, c);
    } catch(final Exception e) {
      //e.getCause().printStackTrace();
      //System.out.println(hackedXml);
      throw new IllegalArgumentException("Failed to convert xml based object.", e);
    }
  }

  @SuppressWarnings("unchecked")
  public static Object caGridToJaxb(final Object input, final XMLUtility wrapper, final QName qname) {
    Preconditions.checkNotNull(input);
    final StringWriter xmlWriter = new StringWriter();
    SERIALIZATION_UTILS.serialize(xmlWriter, input, qname);
    String xml = xmlWriter.toString();
    xml = xml.replaceAll("</?ns\\d+:InputElement>", "");
    return wrapper.fromString(xml);
  }

  public static Tool convert(final edu.umn.msi.tropix.galaxy.tool.Tool jaxbTool) {
    return (Tool) jaxbToCaGrid(jaxbTool, TOOL_XML_UTILITY, Tool.class);
  }

  public static edu.umn.msi.tropix.galaxy.tool.Tool convert(final Tool caGridTool) {
    return (edu.umn.msi.tropix.galaxy.tool.Tool) caGridToJaxb(caGridTool, TOOL_XML_UTILITY, TOOL_QNAME);
  }

  public static RootInput convert(final edu.umn.msi.tropix.galaxy.inputs.RootInput jaxbInput) {
    return (RootInput) XmlConversionUtils.jaxbToCaGrid(jaxbInput, INPUT_XML_UTILITY, RootInput.class);
  }

  public static edu.umn.msi.tropix.galaxy.inputs.RootInput convert(final RootInput caGridTool) {
    return (edu.umn.msi.tropix.galaxy.inputs.RootInput) XmlConversionUtils.caGridToJaxb(caGridTool, INPUT_XML_UTILITY, ROOT_INPUT_QNAME);
  }

  public static edu.umn.msi.tropix.galaxy.tool.Tool load(final InputStream stream) {
    return TOOL_XML_UTILITY.deserialize(stream);
  }
  
  public static edu.umn.msi.tropix.galaxy.tool.Tool deserialize(final String xml) {
    return TOOL_XML_UTILITY.fromString(xml);
  }
  
  public static String serialize(final edu.umn.msi.tropix.galaxy.tool.Tool tool) {
    return TOOL_XML_UTILITY.toString(tool);
  }

  public static String serialize(final edu.umn.msi.tropix.galaxy.inputs.RootInput rootInput) {
    return INPUT_XML_UTILITY.toString(rootInput);
  }

}

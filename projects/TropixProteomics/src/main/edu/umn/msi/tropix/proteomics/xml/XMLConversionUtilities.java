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

package edu.umn.msi.tropix.proteomics.xml;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.namespace.QName;

import net.sourceforge.sashimi.mzxml.v3_0.MzXML;

import com.google.common.base.Preconditions;

import edu.umn.msi.tropix.proteomics.bioml.Bioml;
import edu.umn.msi.tropix.proteomics.scaffold.input.Scaffold;
import gov.nih.nci.cagrid.common.Utils;

public class XMLConversionUtilities {
  private static final MzXMLUtility MZXML_UTILITY = new MzXMLUtility();
  private static final BiomlUtility BIOML_UTILITY = new BiomlUtility();
  private static final ScaffoldUtility SCAFFOLD_UTILITY = new ScaffoldUtility();

  private static final QName BIOML_QNAME = new QName("http://msi.umn.edu/proteomics/bioml", "bioml");
  private static final QName MZXML_QNAME = new QName("http://sashimi.sourceforge.net/schema_revision/mzXML_3.0", "mzXML");
  private static final QName SCAFFOLD_QNAME = new QName("http://msi.umn.edu/proteomics/scaffold/input", "Scaffold");
  private static final QName SEQUEST_PARAMETERS_QNAME = new QName("gme://edu.umn.msi.tropix/0.1/edu.umn.msi.tropix.models.sequest", "SequestParameters");
  private static final QName XTANDEM_PARAMETERS_QNAME = new QName("gme://edu.umn.msi.tropix/0.1/edu.umn.msi.tropix.models.xtandem", "XTandemParameters");

  public static edu.umn.msi.tropix.models.sequest.SequestParameters convert(final edu.umn.msi.tropix.models.sequest.cagrid.SequestParameters input) {
    try {
      Preconditions.checkNotNull(input);
      final StringWriter xmlWriter = new StringWriter();
      Utils.serializeObject(input, SEQUEST_PARAMETERS_QNAME, xmlWriter);
      final edu.umn.msi.tropix.models.sequest.SequestParameters converted = (edu.umn.msi.tropix.models.sequest.SequestParameters) edu.umn.msi.tropix.models.sequest.xml.XMLUtility.getSingletonInstance().fromXML(xmlWriter.toString());
      return converted;
    } catch(final Exception e) {
      throw new IllegalArgumentException("Failed to convert xml based object.", e);
    }
  }

  public static edu.umn.msi.tropix.models.sequest.cagrid.SequestParameters convert(final edu.umn.msi.tropix.models.sequest.SequestParameters input) {
    try {
      final String xml = edu.umn.msi.tropix.models.sequest.xml.XMLUtility.getSingletonInstance().toXML(input);
      return (edu.umn.msi.tropix.models.sequest.cagrid.SequestParameters) Utils.deserializeObject(new StringReader(xml), edu.umn.msi.tropix.models.sequest.cagrid.SequestParameters.class);
    } catch(final Exception e) {
      throw new IllegalArgumentException("Failed to convert xml based object.", e);
    }
  }

  public static edu.umn.msi.tropix.models.xtandem.XTandemParameters convert(final edu.umn.msi.tropix.models.xtandem.cagrid.XTandemParameters input) {
    try {
      Preconditions.checkNotNull(input);
      final StringWriter xmlWriter = new StringWriter();
      Utils.serializeObject(input, XTANDEM_PARAMETERS_QNAME, xmlWriter);
      return (edu.umn.msi.tropix.models.xtandem.XTandemParameters) edu.umn.msi.tropix.models.xtandem.xml.XMLUtility.getSingletonInstance().fromXML(xmlWriter.toString());
    } catch(final Exception e) {
      throw new IllegalArgumentException("Failed to convert xml based object.", e);
    }
  }

  public static edu.umn.msi.tropix.models.xtandem.cagrid.XTandemParameters convert(final edu.umn.msi.tropix.models.xtandem.XTandemParameters input) {
    try {
      final String xml = edu.umn.msi.tropix.models.xtandem.xml.XMLUtility.getSingletonInstance().toXML(input);
      return (edu.umn.msi.tropix.models.xtandem.cagrid.XTandemParameters) Utils.deserializeObject(new StringReader(xml), edu.umn.msi.tropix.models.xtandem.cagrid.XTandemParameters.class);
    } catch(final Exception e) {
      throw new IllegalArgumentException("Failed to convert xml based object.", e);
    }
  }

  public static Scaffold convert(final edu.umn.msi.tropix.proteomics.scaffold.input.cagrid.Scaffold input) {
    return (Scaffold) edu.umn.msi.tropix.grid.xml.XmlConversionUtils.caGridToJaxb(input, SCAFFOLD_UTILITY, SCAFFOLD_QNAME);
  }

  public static edu.umn.msi.tropix.proteomics.scaffold.input.cagrid.Scaffold convert(final Scaffold input) {
    return (edu.umn.msi.tropix.proteomics.scaffold.input.cagrid.Scaffold) edu.umn.msi.tropix.grid.xml.XmlConversionUtils.jaxbToCaGrid(input, SCAFFOLD_UTILITY, edu.umn.msi.tropix.proteomics.scaffold.input.cagrid.Scaffold.class);
  }

  public static MzXML convert(final net.sourceforge.sashimi.mzxml.v3_0.cagrid.MzXML input) {
    return (MzXML) edu.umn.msi.tropix.grid.xml.XmlConversionUtils.caGridToJaxb(input, MZXML_UTILITY, MZXML_QNAME);
  }

  public static net.sourceforge.sashimi.mzxml.v3_0.cagrid.MzXML convert(final MzXML input) {
    return (net.sourceforge.sashimi.mzxml.v3_0.cagrid.MzXML) edu.umn.msi.tropix.grid.xml.XmlConversionUtils.jaxbToCaGrid(input, MZXML_UTILITY, net.sourceforge.sashimi.mzxml.v3_0.cagrid.MzXML.class);
  }

  public static Bioml convert(final edu.umn.msi.tropix.proteomics.bioml.cagrid.Bioml input) {
    return (Bioml) edu.umn.msi.tropix.grid.xml.XmlConversionUtils.caGridToJaxb(input, BIOML_UTILITY, BIOML_QNAME);
  }

  public static edu.umn.msi.tropix.proteomics.bioml.cagrid.Bioml convert(final Bioml input) {
    return (edu.umn.msi.tropix.proteomics.bioml.cagrid.Bioml) edu.umn.msi.tropix.grid.xml.XmlConversionUtils.jaxbToCaGrid(input, BIOML_UTILITY, edu.umn.msi.tropix.proteomics.bioml.cagrid.Bioml.class);
  }

}

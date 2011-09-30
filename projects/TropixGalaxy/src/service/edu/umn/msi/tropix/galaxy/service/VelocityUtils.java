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

package edu.umn.msi.tropix.galaxy.service;

import java.io.StringReader;
import java.io.Writer;
import java.util.UUID;

import org.apache.velocity.Template;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.RuntimeSingleton;
import org.apache.velocity.runtime.parser.ParseException;
import org.apache.velocity.runtime.parser.node.SimpleNode;

/**
 * Provides abstractions for dealing with Apache Velocity system.
 * 
 * @author John Chilton
 *
 */
class VelocityUtils {

  static void evaluate(final String templateStr, final org.apache.velocity.context.Context context, final Writer writer) {
    final Template template = VelocityUtils.getTemplateForString(templateStr);    
    VelocityUtils.merge(template, context, writer);
  }
  
  static void merge(final Template template, final org.apache.velocity.context.Context context, final Writer writer) {
    try {
      template.merge(context, writer);
    } catch(final Exception e) {
      throw new RuntimeException(e);
    }
  }
  
  static Template getTemplateForString(final String templateStr) {
    RuntimeServices runtimeServices = RuntimeSingleton.getRuntimeServices();            
    StringReader reader = new StringReader(templateStr);
    SimpleNode node;
    try {
      node = runtimeServices.parse(reader, UUID.randomUUID().toString());
    } catch(final ParseException e) {
      throw new RuntimeException(e);
    }
    Template template = new Template();
    template.setRuntimeServices(runtimeServices);
    template.setData(node);
    template.initDocument();
    return template;
  }
  
}

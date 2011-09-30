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

import java.io.StringWriter;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.testng.annotations.Test;

public class VelocityUtilsTest {

  @Test(groups = "unit")
  public void testVelocity() {
    final VelocityContext context =  new VelocityContext();
    context.put("moo", "cow");
    
    final StringWriter writer = new StringWriter();
    VelocityUtils.evaluate("$moo = cow", context, writer);
    assert writer.toString().equals("cow = cow") : writer.toString();
    
  }
  
  @Test(groups = "unit", expectedExceptions = RuntimeException.class)
  public void testParseException() {
    VelocityUtils.getTemplateForString("#foreach(moo in )");
  }

  public static class ErrorObject {
    public String getFoo() {
      throw new RuntimeException();
    }
  }
  
  @Test(groups = "unit", expectedExceptions = RuntimeException.class)
  public void testMergeException() {
    final Template template = VelocityUtils.getTemplateForString("$foo.getFoo()");
    final VelocityContext context =  new VelocityContext();
    context.put("foo", new ErrorObject());
    final StringWriter writer = new StringWriter();
    VelocityUtils.merge(template, context, writer);
  }
  
}

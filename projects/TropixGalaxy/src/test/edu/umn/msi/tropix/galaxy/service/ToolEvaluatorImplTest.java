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

import java.util.Map;

import org.testng.annotations.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import edu.umn.msi.tropix.galaxy.tool.Command;
import edu.umn.msi.tropix.galaxy.tool.ConfigFile;
import edu.umn.msi.tropix.galaxy.tool.ConfigFiles;
import edu.umn.msi.tropix.galaxy.tool.Tool;

public class ToolEvaluatorImplTest {

  public static class Input {
    private final String value;
    private final Map<String, Object> map; 
    
    public Input(final String value) {
      this.value = value;
      this.map = null;
    }
    
    public Input(final String value, final Map<String, Object> map) {
      this.value = value;
      this.map = map;
    }
    
    public Object get(final String value) {
      return map.get(value);
    }
    
    public String toString() {
      return value;
    }
    
  }
  
  @Test(groups = "unit")
  public void testEvaluator() {    
    final Context context = new Context();
    context.put("a", "1");
    context.put("b", "2");    
    final Map<String, Object> objects = Maps.newHashMap();
    objects.put("coo", "4");
    context.put("c", new Context("3", objects));
    
    final ToolEvaluatorImpl evaluator = new ToolEvaluatorImpl();
    final Tool tool = new Tool();
    final Command command = new Command();
    command.setValue("moo cow $a ${b} ${c.coo}");
    tool.setCommand(command);

    final ConfigFile configFile1 = new ConfigFile();
    configFile1.setValue("this is $c");
    final ConfigFile configFile2 = new ConfigFile();
    configFile2.setValue("moo cow");
    tool.setConfigfiles(new ConfigFiles());
    tool.getConfigfiles().getConfigfile().addAll(Lists.newArrayList(configFile1, configFile2));
    evaluator.resolve(tool, context);

    assert tool.getCommand().getValue().equals("moo cow 1 2 4");
    assert tool.getConfigfiles().getConfigfile().get(0).getValue().equals("this is 3");
    assert tool.getConfigfiles().getConfigfile().get(1).getValue().equals("moo cow");
  }

}

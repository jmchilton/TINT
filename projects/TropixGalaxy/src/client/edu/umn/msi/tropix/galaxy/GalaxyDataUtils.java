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

package edu.umn.msi.tropix.galaxy;

import java.util.List;
import java.util.Map;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import edu.umn.msi.tropix.galaxy.inputs.Input;
import edu.umn.msi.tropix.galaxy.inputs.RootInput;
import edu.umn.msi.tropix.galaxy.tool.Conditional;
import edu.umn.msi.tropix.galaxy.tool.ConditionalWhen;
import edu.umn.msi.tropix.galaxy.tool.InputType;
import edu.umn.msi.tropix.galaxy.tool.Param;
import edu.umn.msi.tropix.galaxy.tool.ParamType;
import edu.umn.msi.tropix.galaxy.tool.Repeat;
import edu.umn.msi.tropix.galaxy.tool.Tool;

public class GalaxyDataUtils {

  private abstract static class TreeWalker<T> {

    protected abstract Iterable<T> getTopLevelItems();

    protected abstract Iterable<T> getChildren(T data);

    protected abstract String getLabel(T data);

    private class Context {
      private String label;
      private T data;

      Context(final String label, final T data) {
        this.label = label;
        this.data = data;
      }

    }

    Map<String, T> flattenTree() {
      final Map<String, T> map = Maps.newHashMap();
      final List<Context> stack = Lists.newLinkedList();
      for(final T data : getTopLevelItems()) {
        stack.add(new Context(getLabel(data), data));
      }
      while(!stack.isEmpty()) {
        final Context context = stack.remove(0);
        map.put(context.label, context.data);
        for(final T children : getChildren(context.data)) {
          stack.add(new Context(context.label + '.' + getLabel(children), children));
        }
      }
      return map;
    }
  }

  private static class InputWalker extends TreeWalker<Input> {
    private final RootInput rootInput;

    InputWalker(final RootInput rootInput) {
      this.rootInput = rootInput;
    }

    protected Iterable<Input> getChildren(final Input data) {
      return data.getInput();
    }

    protected String getLabel(final Input data) {
      return data.getName();
    }

    protected Iterable<Input> getTopLevelItems() {
      return rootInput.getInput();
    }

  }

  private static class ParamTreeWalker extends TreeWalker<InputType> {
    private final Tool tool;

    ParamTreeWalker(final Tool tool) {
      this.tool = tool;
    }

    protected Iterable<InputType> getChildren(final InputType data) {
      List<InputType> children = Lists.newArrayList();
      if(data instanceof Conditional) {
        final Conditional conditional = (Conditional) data;
        children.add(conditional.getParam());
        final List<ConditionalWhen> whens = conditional.getWhen();
        for(final ConditionalWhen when : whens) {
          children.addAll(when.getInputElement());
        }
      } else if(data instanceof Repeat) {
        children = ((Repeat) data).getInputElement();
      }
      return children;
    }

    protected String getLabel(final InputType data) {
      return data.getName();
    }

    protected Iterable<InputType> getTopLevelItems() {
      return tool.getInputs().getInputElement();
    }

  }

  public interface ParamVisitor {
    void visit(final String key, final Input input, final Param param);
  }

  public static void visitParams(final Tool tool, final RootInput rootInput, final ParamVisitor visitor) {
    final Map<String, Input> inputs = GalaxyDataUtils.buildInputMap(rootInput);
    final Map<String, InputType> params = GalaxyDataUtils.buildParamMap(tool);
    for(final Map.Entry<String, Input> input : inputs.entrySet()) {
      final String key = input.getKey();
      final InputType inputDefinition = params.get(key);
      if(inputDefinition instanceof Param) {
        final Param param = (Param) inputDefinition;
        visitor.visit(key, input.getValue(), param);
      }
    }
  }

  public static Map<String, Input> buildInputMap(final RootInput rootInput) {
    return new InputWalker(rootInput).flattenTree();
  }

  public static Map<String, InputType> buildParamMap(final Tool tool) {
    return new ParamTreeWalker(tool).flattenTree();
  }
  
  
  public static final Predicate<InputType> DATA_PARAM_PREDICATE = new Predicate<InputType>() {

    public boolean apply(final InputType input) {
      boolean isDataParam = false;
      if(input instanceof Param) {
        final Param param = (Param) input;
        isDataParam = param.getType() == ParamType.DATA;
      }
      return isDataParam;
    }
    
  };
  /*
  public static void visitDataParams(final Tool tool, final Closure<Param> closure) {
    for(final InputType inputType : Iterables.filter(buildParamMap(tool).values(), DATA_PARAM_PREDICATE)) {
      closure.apply((Param) inputType);
    }
  }
  */
}

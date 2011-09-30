package edu.umn.msi.tropix.galaxy.service;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

import edu.umn.msi.tropix.galaxy.inputs.Input;
import edu.umn.msi.tropix.galaxy.inputs.RootInput;
import edu.umn.msi.tropix.galaxy.tool.Conditional;
import edu.umn.msi.tropix.galaxy.tool.ConditionalWhen;
import edu.umn.msi.tropix.galaxy.tool.Data;
import edu.umn.msi.tropix.galaxy.tool.InputType;
import edu.umn.msi.tropix.galaxy.tool.Param;
import edu.umn.msi.tropix.galaxy.tool.Tool;

public class ContextBuilder {
  
  private static void populateInputs(final Iterable<InputType> inputs, final Iterable<Input> inputValues, final Map<String, Object> map) {
    for(InputType type : inputs) {
      final String name = type.getName();
      final Input inputValue = findInput(name, inputValues);
      final Context tree = buildContextForInput(type, inputValue);
      map.put(name, tree);
    }
  }
  
  private static Context buildContextForInput(final InputType inputType, @Nullable final Input inputValue) {
    final Map<String, Object> inputProperties = Maps.newHashMap();
    String value = null;
    if(inputType instanceof Param) {
      final Param param = new Param();
      inputProperties.put("label", new Context(param.getLabel()));
      value = inputValue != null ? inputValue.getValue() : null;
    } else if(inputType instanceof Conditional) {
      final Conditional conditional = (Conditional) inputType;
      final Param param = conditional.getParam();
      final Context paramTree = buildContextForInput(param, findInput(param.getName(), inputValue.getInput()));
      inputProperties.put(param.getName(), paramTree);
      for(ConditionalWhen when : conditional.getWhen()) {
        populateInputs(when.getInputElement(), inputValue.getInput(), inputProperties);
      }
    }
    final Context tree = new Context(value, inputProperties);
    return tree;
  }
  
  private static Input findInput(final String inputName, final Iterable<Input> inputs) {
    Preconditions.checkNotNull(inputName);
    return Iterables.find(inputs, new Predicate<Input>() {
      public boolean apply(final Input queryInput) {
        String queryInputName = queryInput.getName();
        return inputName.equals(queryInputName);
      }      
    });
  }

  public Context buildContext(final Tool tool, final RootInput rootInput, final Map<String, String> outputMap) {
    final Map<String, Object> contextContents = Maps.newHashMap();
    
    final List<InputType> inputElements = tool.getInputs().getInputElement();
    populateInputs(inputElements, rootInput.getInput(), contextContents);
    
    final List<Data> outputElements = tool.getOutputs().getData();
    populateOutputs(outputMap, contextContents, outputElements);
    
    final Context context = new Context(contextContents);
    return context;
  }

  private static void populateOutputs(final Map<String, String> outputMap, final Map<String, Object> contextContents, final List<Data> outputElements) {
    for(final Data output : outputElements) {
      final String format = StringUtils.defaultString(output.getFormat());
      final String label = StringUtils.defaultString(output.getLabel());

      final Map<String, Object> map = Maps.newHashMap();
      map.put("format", format);
      map.put("label", label);
      
      final Context tree = new Context(outputMap.get(output.getName()), map);
      contextContents.put(output.getName(), tree);
    }
  }

}

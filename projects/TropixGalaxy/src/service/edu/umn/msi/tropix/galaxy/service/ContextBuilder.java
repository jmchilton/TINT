package edu.umn.msi.tropix.galaxy.service;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import edu.umn.msi.tropix.galaxy.GalaxyDataUtils;
import edu.umn.msi.tropix.galaxy.inputs.Input;
import edu.umn.msi.tropix.galaxy.inputs.RootInput;
import edu.umn.msi.tropix.galaxy.tool.Conditional;
import edu.umn.msi.tropix.galaxy.tool.ConditionalWhen;
import edu.umn.msi.tropix.galaxy.tool.Data;
import edu.umn.msi.tropix.galaxy.tool.InputType;
import edu.umn.msi.tropix.galaxy.tool.Param;
import edu.umn.msi.tropix.galaxy.tool.Repeat;
import edu.umn.msi.tropix.galaxy.tool.Tool;

public class ContextBuilder {

  private static void populateInputs(final Iterable<InputType> inputs, final Iterable<Input> inputValues, final Map<String, Object> map) {
    populateInputs(inputs, inputValues, map, false);
  }

  private static void populateInputs(final Iterable<InputType> inputs, final Iterable<Input> inputValues, final Map<String, Object> map,
      final boolean allowNull) {
    for(InputType type : inputs) {
      final String name = type.getName();
      final Input inputValue = GalaxyDataUtils.findInput(name, inputValues, allowNull);
      final Object tree = buildContextForInput(type, inputValue);
      map.put(name, tree);
    }
  }

  private static Object buildContextForInput(final InputType inputType, @Nullable final Input inputValue) {
    final Object tree;
    final Map<String, Object> inputProperties = Maps.newHashMap();
    String value = null;
    if(inputType instanceof Param) {
      final Param param = (Param) inputType;
      inputProperties.put("label", new Context(param.getLabel()));
      value = inputValue != null ? inputValue.getValue() : null;
      tree = new Context(value, inputProperties);
    } else if(inputType instanceof Conditional) {
      final Conditional conditional = (Conditional) inputType;
      final Param param = conditional.getParam();
      final Context paramTree = (Context) buildContextForInput(param, GalaxyDataUtils.findInput(param.getName(), inputValue.getInput()));
      inputProperties.put(param.getName(), paramTree);
      for(ConditionalWhen when : conditional.getWhen()) {
        populateInputs(when.getInputElement(), inputValue.getInput(), inputProperties, true);
      }
      tree = new Context(value, inputProperties);
    } else if(inputType instanceof Repeat) {
      final Repeat repeat = (Repeat) inputType;
      final List<Context> contexts = Lists.newArrayList();
      final Input repeatInput = inputValue; // GalaxyDataUtils.findInput(repeat.getName(), inputValue.getInput());
      for(Input repeatInstance : repeatInput.getInput()) {
        final Map<String, Object> repeatInstanceProperties = Maps.newHashMap();
        populateInputs(repeat.getInputElement(), repeatInstance.getInput(), repeatInstanceProperties);
        final Context repeatInstanceContext = new Context(GalaxyDataUtils.REPEAT_INSTANCE, repeatInstanceProperties);
        contexts.add(repeatInstanceContext);
      }
      tree = contexts;
      // inputProperties.put(repeat.getName(), contexts);
    } else {
      throw new IllegalStateException("Unknown inputType " + inputType.getClass().getName());
    }
    return tree;
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

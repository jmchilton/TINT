package edu.umn.msi.tropix.galaxy.service;

import com.google.common.collect.Lists;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.galaxy.GalaxyDataUtils;

public class CheetahTemplateEvaluatorTest {

  private void assertEvaluatesTo(final String template, final Context context, final String equals) {
    final String obtained = CheetahTemplateEvaluator.evaluate(template, context);
    assert obtained.equals(equals) : String.format("Expect <%s>, obtained <%s>.", equals, obtained);
  }
  
  @Test
  public void testString() {
    assertEvaluatesTo("Hello", new Context(), "Hello");
  }
  
  @Test
  public void testReplace() {
    final Context context = new Context();
    context.put("world", "World");
    assertEvaluatesTo("Hello $world!", context, "Hello World!");
  }
  
  @Test
  public void testNestedEvaluation() {
    final Context context = new Context();
    final Context nestedContext = new Context("C VALUE");
    nestedContext.put("sub", "SUB VALUE");
    context.put("c", nestedContext);
    assertEvaluatesTo("$c $c.sub", context, "C VALUE SUB VALUE");
  }
  
  @Test
  public void testList() {
    final Context context = new Context();
    final Context instanceContext = new Context(GalaxyDataUtils.REPEAT_INSTANCE);
    instanceContext.put("input", "a");
    context.put("queries", Lists.newArrayList(instanceContext));
    assertEvaluatesTo("#for $q in $queries\n  ${q.input}#end for", context, "  a");
  }
  
}

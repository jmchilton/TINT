package edu.umn.msi.tropix.galaxy.service;

import java.io.StringWriter;

import org.testng.annotations.Test;

public class CheetahTemplateEvaluatorTest {

  private void assertEvaluatesTo(final String template, final Context context, final String equals) {
    final StringWriter writer = new StringWriter();
    CheetahTemplateEvaluator.evaluate(template, context, writer);
    final String obtained = writer.toString();
    assert obtained.equals(equals) : String.format("Expect %s, obtained %s.", equals, obtained);
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
  
  
  
  
}

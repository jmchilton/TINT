package edu.umn.msi.tropix.webgui.functional;

import com.google.common.base.Supplier;

import static java.lang.String.format;

public class ScLocator implements Supplier<String> {
  private final String expression;
  
  public ScLocator(final String expression) {
    this.expression = expression;
  }
  
  public static ScLocator withRoot(final String objectType, final String objectId) {
    return new ScLocator(format("scLocator=//%s[ID=\"%s\"]", objectType, objectId));
  }
  
  public static ScLocator progressListGrid(final int index) {
    return listGrid("ProgressListGrid").row(index);
  }
  
  public ScLocator row(final int index) {
    return new ScLocator(format("%s/body/row[%d]", expression, index));
  }
  
  public static ScLocator listGrid(final String objectId) {
    return withRoot("ListGrid", objectId);
  }
  
  public static ScLocator form(final String objectId) {
    return withRoot("DynamicForm", objectId);
  }
  
  public ScLocator index(final int index) {
    return new ScLocator(format("%s[%d]", expression, index));
  }
  
  public ScLocator child(final String childName) {
    return new ScLocator(format("%s/%s", expression, childName));
  }
  
  public ScLocator childWithIndex(final String childName, final int index) {
    return new ScLocator(format("%s/%s[%d]", expression, childName, index));
  }
  
  public ScLocator childMatching(final String childName, final String attributeExpression) {
    return new ScLocator(format("%s/%s[%s]", expression, childName, attributeExpression));
  }

  public String get() {
    return expression;
  }
  
  
}

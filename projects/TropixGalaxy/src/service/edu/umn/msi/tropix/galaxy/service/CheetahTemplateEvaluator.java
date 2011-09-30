package edu.umn.msi.tropix.galaxy.service;

import java.io.IOException;
import java.io.Writer;

import org.python.core.Py;
import org.python.core.PyObject;
import org.python.core.PyString;

public class CheetahTemplateEvaluator {  
    
  public static void evaluate(final String template, final Context context, final Writer writer) {
    try {
      final String evalutedStr = evaluate(template, context);
      writer.append(evalutedStr);
    } catch(IOException e) {
      throw new RuntimeException(e);
    }
  }  

  public static String evaluate(final String template, final Context context) {
    final PyObject templateClass = JythonEnv.getClass("Template");
    final PyObject pyObjectContext = context.asPyObject();
    final PyString pyStringTemplate = new PyString(template);
    final PyObject[] templateArgs = new PyObject[] {pyStringTemplate, pyObjectContext};
    final String[] templateArgKeywords = new String[] {"source", "searchList"};
    final PyObject templateObject = templateClass.__call__(templateArgs, templateArgKeywords);
    final String evalutedStr = Py.tojava(templateObject.__str__(), String.class);
    return evalutedStr;
  } 
  
}

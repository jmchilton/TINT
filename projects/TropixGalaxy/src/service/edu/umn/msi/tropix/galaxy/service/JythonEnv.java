package edu.umn.msi.tropix.galaxy.service;

import org.python.core.PyObject;
import org.python.core.PySystemState;
import org.python.util.PythonInterpreter;

public class JythonEnv {
  private static PythonInterpreter interp;

  static synchronized void init() {
    if(interp == null) {
      PySystemState.initialize();
      interp = new PythonInterpreter();
      interp.exec("from Cheetah.Template import Template");
      interp.exec("from named_odict import named_odict");
    }
  }

  static PythonInterpreter getInterpreter() {
    init();
    return interp;
  }
  
  static PyObject getClass(final String name) {
    return getInterpreter().get(name);
  }
  
}

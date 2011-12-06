package edu.umn.msi.tropix.common.execution;

import edu.umn.msi.tropix.common.execution.impl.ExecutionFactoryImpl;

public class ExecutionFactories {
  
  public static ExecutionFactory getDefaultExecutionFactory() {
    return new ExecutionFactoryImpl();
  }
}

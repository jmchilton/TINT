package edu.umn.msi.tropix.cloud.forwarding;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.execution.ExecutionCallback;
import edu.umn.msi.tropix.common.execution.ExecutionConfiguration;
import edu.umn.msi.tropix.common.execution.ExecutionFactory;
import edu.umn.msi.tropix.common.execution.ExecutionHook;
import edu.umn.msi.tropix.common.test.EasyMockUtils;

public class IptablesExecutorImplTest {
  private int returnValue;
  private IptablesExecutorImpl executor;
  private Capture<ExecutionConfiguration> executionConfigurationCapture;
  private ExecutionHook executionHook;
  private ExecutionFactory executionFactory;
  
  @BeforeMethod(groups = "unit")
  public void init() {
    executor = new IptablesExecutorImpl();
    executionFactory = EasyMock.createMock(ExecutionFactory.class);
    executor.setExecutionFactory(executionFactory);
    executionConfigurationCapture = EasyMockUtils.newCapture();
    executionHook = EasyMock.createMock(ExecutionHook.class);
    EasyMock.expect(executionFactory.execute(EasyMock.capture(executionConfigurationCapture), EasyMock.isNull(ExecutionCallback.class))).andReturn(executionHook);
    executionHook.waitFor();
    returnValue = 0;
    EasyMock.expect(executionHook.getReturnValue()).andStubAnswer(new IAnswer<Integer>(){
      public Integer answer() throws Throwable {
        return returnValue;
      }
    });
    EasyMock.replay(executionFactory, executionHook);
  }
  
  
  @Test(groups = "unit", expectedExceptions = RuntimeException.class)
  public void testChecksReturnValue() {
    returnValue = 1;
    executor.executeWithArguments(new String[] {"-L"});
  }
  
  @Test(groups = "unit") 
  public void testCallsSudo() {
    executor.executeWithArguments(new String[] {"-L"});
    assert executionConfigurationCapture.getValue().getApplication().equals(IptablesExecutorImpl.DEFAULT_SUDO_COMMAND);
  }

  @Test(groups = "unit") 
  public void testCallsIptables() {
    executor.executeWithArguments(new String[] {"-L"});
    assert executionConfigurationCapture.getValue().getArguments().get(0).equals(IptablesExecutorImpl.DEFAULT_IPTABLES_PATH);
  }
  
  @Test(groups = "unit") 
  public void testCallsWithCorrectArgs() {
    executor.executeWithArguments(new String[] {"-L"});
    assert executionConfigurationCapture.getValue().getArguments().get(1).equals("-L");
  }


}

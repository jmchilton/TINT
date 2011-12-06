package edu.umn.msi.tropix.cloud.forwarding;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.cloud.forwarding.PortRedirecter.RedirectionInformation;
import edu.umn.msi.tropix.common.test.EasyMockUtils;

public class PortRedirecterImplTest {
  
  private static final int TEST_OUTGOING_PORT = 456;
  private static final String TEST_OUTGOING_ADDRESS = "55.66.77.88";
  private static final int TEST_INCOMING_PORT = 123;
  private static final String TEST_INCOMING_ADDRESS = "11.22.33.44";
  private Capture<String[]> argCapture;

  @BeforeMethod(groups = "unit")
  public void init() {
    argCapture = EasyMockUtils.newCapture();
    final IptablesExecutor itablesExecutor = EasyMock.createMock(IptablesExecutor.class);
    itablesExecutor.executeWithArguments(EasyMock.capture(argCapture));
    EasyMock.replay(itablesExecutor);
    final PortRedirecterImpl redirecter = new PortRedirecterImpl(itablesExecutor);
    final RedirectionInformation redirectionInformation = new RedirectionInformation();
    redirectionInformation.setIncomingAddress(TEST_INCOMING_ADDRESS);
    redirectionInformation.setIncomingPort(TEST_INCOMING_PORT);
    redirectionInformation.setOutgoingAddress(TEST_OUTGOING_ADDRESS);
    redirectionInformation.setOutgoingPort(TEST_OUTGOING_PORT);
    redirecter.redirect(redirectionInformation);
    
  }
  
  private void hasArgument(final String argumentName, final Object value) {
    final String[] arguments = argCapture.getValue();
    boolean hasArgument = false;
    for(int i = 0; i < arguments.length - 1; i++) {
      if(argumentName.equals(arguments[i])) {
        Assert.assertEquals(arguments[i+1], value);
        hasArgument = true;
        break;
      }
    }
    assert hasArgument;
  }

  @Test(groups = "unit")
  public void testPreroutes() {
    hasArgument("-A", "PREROUTING");
  }
  
  @Test(groups = "unit")
  public void testTypeNat() {
    hasArgument("-t", "nat");
  }
  
  @Test(groups = "unit")
  public void testRedirectsSource() {
    hasArgument("--source", TEST_INCOMING_ADDRESS);
  }
  
  @Test(groups = "unit")
  public void testRedirectPort() {
    hasArgument("--dport", "" + TEST_INCOMING_PORT);
  }

  @Test(groups = "unit")
  public void testDestination() {
    hasArgument("--to", String.format("%s:%d", TEST_OUTGOING_ADDRESS, TEST_OUTGOING_PORT));
  }  
  
}

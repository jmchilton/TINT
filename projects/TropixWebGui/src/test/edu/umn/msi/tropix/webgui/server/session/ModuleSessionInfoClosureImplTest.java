package edu.umn.msi.tropix.webgui.server.session;

import org.testng.annotations.Test;

import edu.umn.msi.tropix.webgui.client.utils.Lists;
import edu.umn.msi.tropix.webgui.services.session.Module;
import edu.umn.msi.tropix.webgui.services.session.SessionInfo;

public class ModuleSessionInfoClosureImplTest extends BaseSessionInfoClosureImplTest {

  @Test(groups = "unit")
  public void testSetModules() {
    final Iterable<String> modules = Lists.newArrayList(Module.SHARING.toString(), Module.PROTIP.toString());
    final ModuleSessionInfoClosureImpl closure = new ModuleSessionInfoClosureImpl(modules);
    final SessionInfo sessionInfo = get(closure);
    assert sessionInfo.getModules().contains(Module.SHARING);
    assert sessionInfo.getModules().contains(Module.PROTIP);
    assert !sessionInfo.getModules().contains(Module.GUEST);
  }
}

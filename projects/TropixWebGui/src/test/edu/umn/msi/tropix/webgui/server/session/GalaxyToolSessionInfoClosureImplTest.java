package edu.umn.msi.tropix.webgui.server.session;

import java.util.UUID;

import org.easymock.EasyMock;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.models.GalaxyTool;
import edu.umn.msi.tropix.persistence.service.GalaxyToolService;
import edu.umn.msi.tropix.webgui.services.session.SessionInfo;

public class GalaxyToolSessionInfoClosureImplTest extends BaseSessionInfoClosureImplTest {

  @Test(groups = "unit")
  public void testGroupsPopulated() {
    super.init();
    final GalaxyToolService galaxyToolService = EasyMock.createMock(GalaxyToolService.class);

    final GalaxyToolSessionInfoClosureImpl closure = new GalaxyToolSessionInfoClosureImpl(galaxyToolService, getUserSession(), getSanitizer());

    final GalaxyTool tool1 = new GalaxyTool(), tool2 = new GalaxyTool();
    tool1.setId(UUID.randomUUID().toString());
    tool2.setId(UUID.randomUUID().toString());
    EasyMock.expect(galaxyToolService.list(getUserId())).andStubReturn(new GalaxyTool[] {tool1, tool2});
    EasyMock.replay(galaxyToolService);
    final SessionInfo sessionInfo = get(closure);
    assert sessionInfo.getGalaxyTools().contains(tool1);
    assert sessionInfo.getGalaxyTools().contains(tool2);
    assert getSanitizer().wasSanitized(tool1);
    assert getSanitizer().wasSanitized(tool2);
  }

}

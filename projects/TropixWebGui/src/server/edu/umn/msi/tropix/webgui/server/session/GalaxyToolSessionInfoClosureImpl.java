package edu.umn.msi.tropix.webgui.server.session;

import java.util.Arrays;
import java.util.List;

import javax.annotation.ManagedBean;
import javax.inject.Inject;

import com.google.common.collect.Lists;

import edu.umn.msi.tropix.models.GalaxyTool;
import edu.umn.msi.tropix.persistence.service.GalaxyToolService;
import edu.umn.msi.tropix.webgui.server.models.BeanSanitizer;
import edu.umn.msi.tropix.webgui.server.models.BeanSanitizerUtils;
import edu.umn.msi.tropix.webgui.server.security.UserSession;
import edu.umn.msi.tropix.webgui.services.session.SessionInfo;

@ManagedBean
public class GalaxyToolSessionInfoClosureImpl implements SessionInfoClosure {
  private final GalaxyToolService galaxyToolService;
  private final UserSession userSession;
  private final BeanSanitizer beanSanitizer;

  @Inject
  public GalaxyToolSessionInfoClosureImpl(final GalaxyToolService galaxyToolService,
                                          final UserSession userSession,
                                          final BeanSanitizer beanSanitizer) {
    this.galaxyToolService = galaxyToolService;
    this.userSession = userSession;
    this.beanSanitizer = beanSanitizer;
  }

  public void apply(final SessionInfo sessionInfo) {
    final GalaxyTool[] toolArray = galaxyToolService.list(userSession.getGridId());
    final List<GalaxyTool> tools = Lists.newArrayList(Lists.transform(Arrays.asList(toolArray),
        BeanSanitizerUtils.<GalaxyTool>asFunction(beanSanitizer)));
    sessionInfo.setGalaxyTools(tools);
  }

}

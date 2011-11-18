package edu.umn.msi.tropix.webgui.server;

import javax.inject.Inject;

import org.python.google.common.base.Joiner;
import org.springframework.util.StringUtils;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.client.services.Constants;
import edu.umn.msi.tropix.common.test.ConfigDirBuilder;
import edu.umn.msi.tropix.models.FileType;
import edu.umn.msi.tropix.persistence.service.FileTypeService;
import edu.umn.msi.tropix.webgui.client.ConfigurationOptions;
import edu.umn.msi.tropix.webgui.services.session.Module;
import edu.umn.msi.tropix.webgui.services.session.SessionInfo;

public class SessionInfoIntegrationTest extends WebIntegrationTest {

  @Inject
  private FileTypeService fileTypeService;

  protected void initializeConfigDir(final ConfigDirBuilder configDirBuilder) {
    final ConfigDirBuilder clientDirBuilder = configDirBuilder.createSubConfigDir("client");
    clientDirBuilder.addDeployProperty("modules", Joiner.on(",").join(Module.SHARING.toString(), Module.PROTIP.toString()));
  }

  @Test(groups = "integration")
  public void saveAndLoad() throws InterruptedException {
    logon();
    final SessionInfo sessionInfo = getSessionInfo();
    final FileType[] fileTypes = fileTypeService.listFileTypes(getUser().getCagridId());
    for(final FileType fileType : fileTypes) {
      assert sessionInfo.getFileTypes().contains(fileType);
    }
    assert StringUtils.hasText(sessionInfo.getSessionId());
    assert sessionInfo.getModules().contains(Module.PROTIP);
    assert sessionInfo.getConfigurationOptions().containsKey(ConfigurationOptions.serviceEnabled(Constants.SCAFFOLD));
  }

}

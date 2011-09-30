package edu.umn.msi.tropix.webgui.server.session;

import java.util.Arrays;

import javax.annotation.ManagedBean;
import javax.inject.Inject;

import com.google.common.collect.Lists;

import edu.umn.msi.tropix.models.FileType;
import edu.umn.msi.tropix.persistence.service.FileTypeService;
import edu.umn.msi.tropix.webgui.server.models.BeanSanitizer;
import edu.umn.msi.tropix.webgui.server.models.BeanSanitizerUtils;
import edu.umn.msi.tropix.webgui.server.security.UserSession;
import edu.umn.msi.tropix.webgui.services.session.SessionInfo;

@ManagedBean
public class FileTypeSessionInfoClosureImpl implements SessionInfoClosure {
  private FileTypeService fileTypeService;
  private UserSession userSession;
  private BeanSanitizer sanitizer;

  @Inject
  public FileTypeSessionInfoClosureImpl(final FileTypeService fileTypeService,
                                        final UserSession userSession,
                                        final BeanSanitizer sanitizer) {
    this.fileTypeService = fileTypeService;
    this.userSession = userSession;
    this.sanitizer = sanitizer;
  }

  public void apply(final SessionInfo sessionInfo) {
    sessionInfo.setFileTypes(Lists.newArrayList(Lists.transform(Arrays.asList(fileTypeService.listFileTypes(userSession.getGridId())),
        BeanSanitizerUtils.<FileType>asFunction(sanitizer))));
  }

}

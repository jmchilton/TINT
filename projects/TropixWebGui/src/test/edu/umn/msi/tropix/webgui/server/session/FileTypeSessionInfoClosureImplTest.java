package edu.umn.msi.tropix.webgui.server.session;

import java.util.UUID;

import org.easymock.EasyMock;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.models.FileType;
import edu.umn.msi.tropix.persistence.service.FileTypeService;
import edu.umn.msi.tropix.webgui.services.session.SessionInfo;

public class FileTypeSessionInfoClosureImplTest extends BaseSessionInfoClosureImplTest {

  @Test(groups = "unit")
  public void testFileTypesPopulated() {
    super.init();
    final FileTypeService fileTypeService = EasyMock.createMock(FileTypeService.class);

    final FileType fileType1 = new FileType(), fileType2 = new FileType();
    fileType1.setId(UUID.randomUUID().toString());
    fileType2.setId(UUID.randomUUID().toString());
    EasyMock.expect(fileTypeService.listFileTypes(getUserId())).andStubReturn(new FileType[] {fileType1, fileType2});

    final FileTypeSessionInfoClosureImpl closure = new FileTypeSessionInfoClosureImpl(fileTypeService, getUserSession(), getSanitizer());
    EasyMock.replay(fileTypeService);
    final SessionInfo sessionInfo = get(closure);
    assert sessionInfo.getFileTypes().contains(fileType1);
    assert sessionInfo.getFileTypes().contains(fileType2);
    assert getSanitizer().wasSanitized(fileType1);
    assert getSanitizer().wasSanitized(fileType2);

  }

}

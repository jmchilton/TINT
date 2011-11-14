package edu.umn.msi.tropix.webgui.server.download;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.UUID;

import org.easymock.EasyMock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.test.EasyMockUtils;
import edu.umn.msi.tropix.files.MockPersistentModelStorageDataFactoryImpl;
import edu.umn.msi.tropix.persistence.service.TropixObjectService;
import edu.umn.msi.tropix.proteomics.conversion.MzXMLToMGFConverter;
import edu.umn.msi.tropix.proteomics.conversion.MzXMLToMGFConverter.MgfConversionOptions;
import edu.umn.msi.tropix.storage.client.ModelStorageData;
import edu.umn.msi.tropix.webgui.server.BaseGwtServiceTest;

public class MgfDownloadHelperImplTest extends BaseGwtServiceTest {
  private TropixObjectService tropixObjectService;
  private MockPersistentModelStorageDataFactoryImpl storageDataFactory;
  private MzXMLToMGFConverter mzxmlToMgfConverter;
  private MgfDownloadHelperImpl helper;
  private String objectId;
  
  
  @BeforeMethod(groups = "unit")
  public void init() {
    super.init();
    tropixObjectService = EasyMock.createMock(TropixObjectService.class);
    storageDataFactory = new MockPersistentModelStorageDataFactoryImpl();
    mzxmlToMgfConverter = EasyMock.createMock(MzXMLToMGFConverter.class);
    objectId = UUID.randomUUID().toString();
    helper = new MgfDownloadHelperImpl(getUserSession(), tropixObjectService, storageDataFactory, mzxmlToMgfConverter);
  }
  
  private String expectObjectId() {
    return EasyMock.eq(objectId);
  }
  
  @Test(groups = "unit")
  public void testWrite() {
    final ModelStorageData data = storageDataFactory.getStorageData("service", super.getUserSession().getProxy());
    data.getUploadContext().put("hello world input".getBytes());
    EasyMock.expect(tropixObjectService.getAssociation(expectUserId(), expectObjectId(), EasyMock.eq("mzxml"))).andReturn(data.getTropixFile());
    //final ByteArrayOutputStream outputStream2 = new ByteArrayOutputStream();
    //data.getDownloadContext().get(new File("/home/john/test"));
    //assert new String(outputStream2.toByteArray()).equals("hello world input");
    final ByteArrayInputStream inputStream = new ByteArrayInputStream("hello world2".getBytes());
    mzxmlToMgfConverter.mzxmlToMGF(EasyMockUtils.inputStreamWithContents("hello world input".getBytes()), EasyMockUtils.copy(inputStream), EasyMock.isA(MgfConversionOptions.class));
    
    final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    EasyMock.replay(tropixObjectService, mzxmlToMgfConverter);
    helper.writeMgf(objectId, "DEFAULT", outputStream);
    assert new String(outputStream.toByteArray()).equals("hello world2");
    EasyMock.verify(tropixObjectService, mzxmlToMgfConverter);
  }
  
}

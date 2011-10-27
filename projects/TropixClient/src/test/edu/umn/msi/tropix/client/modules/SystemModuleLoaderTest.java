package edu.umn.msi.tropix.client.modules;

import java.util.List;


import org.easymock.classextension.EasyMock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;

import edu.umn.msi.tropix.models.modules.SystemModule;
import edu.umn.msi.tropix.models.utils.StockFileExtensionI;
import edu.umn.msi.tropix.persistence.service.impl.FileTypeResolver;

public class SystemModuleLoaderTest {
  private List<String> modules;
  private FileTypeResolver fileTypeResolver;
  
  private SystemModuleLoader load() {
    EasyMock.replay(fileTypeResolver);
    return new SystemModuleLoader(modules, fileTypeResolver);
  }
  
  @BeforeMethod(groups = "unit")
  public void init() {
    fileTypeResolver = EasyMock.createMock(FileTypeResolver.class);
    modules = Lists.newArrayList();
  }
  
  @Test(groups = "unit")
  public void testLoadFileTypes() {
    modules.add(SystemModule.PROTIP.toString());
    for(final StockFileExtensionI extension : SystemModule.PROTIP.getFileTypes()) {
      EasyMock.expect(fileTypeResolver.resolveType(extension)).andReturn(null);
    }
    load().init();
    
    EasyMock.verify(fileTypeResolver);
  }
  
  
  
  
}

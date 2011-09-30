package edu.umn.msi.tropix.galaxy.tool.repository;

import org.easymock.classextension.EasyMock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.galaxy.tool.Command;
import edu.umn.msi.tropix.galaxy.tool.Tool;
import edu.umn.msi.tropix.galaxy.xml.GalaxyXmlUtils;
import edu.umn.msi.tropix.persistence.galaxy.PersistentGalaxyToolStore;

public class GalaxyToolRepositoryImplTest {
  private PersistentGalaxyToolStore persistentStore;
  private GalaxyToolRepositoryImpl repository;
  
  @BeforeMethod(groups = "unit")
  public void init() {
    this.persistentStore = EasyMock.createMock(PersistentGalaxyToolStore.class);
    this.repository = new GalaxyToolRepositoryImpl(persistentStore, null);
  }
  
  
  @Test(groups = "unit")
  public void testLoad() {
    final Tool tool = new Tool();
    Command command = new Command();
    command.setValue("echo Moo");
    tool.setCommand(command);
    EasyMock.expect(persistentStore.getXml("test-id")).andStubReturn(GalaxyXmlUtils.serialize(tool));
    EasyMock.replay(persistentStore);
    
  }
  
}

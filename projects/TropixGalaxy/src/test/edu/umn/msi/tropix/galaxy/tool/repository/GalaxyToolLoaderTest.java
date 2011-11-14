package edu.umn.msi.tropix.galaxy.tool.repository;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.easymock.EasyMock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import edu.umn.msi.tropix.common.test.EasyMockUtils;
import edu.umn.msi.tropix.galaxy.tool.Tool;
import edu.umn.msi.tropix.galaxy.xml.GalaxyXmlUtils;
import edu.umn.msi.tropix.models.GalaxyTool;
import edu.umn.msi.tropix.persistence.galaxy.PersistentGalaxyToolStore;

public class GalaxyToolLoaderTest {
  private static class InMemoryToolSource implements ToolSource {
    private List<Tool> tools = Lists.newArrayList();
    
    public Iterable<Tool> getTools() {
      return tools;
    }
    
    void addTool(final Tool tool) {
      this.tools.add(tool);
    }
    
  }
  
  private InMemoryToolSource toolSource;
  private PersistentGalaxyToolStore toolStore;
  private GalaxyToolLoader galaxyToolLoader;
  private List<GalaxyTool> persistedTools;
  
  @BeforeMethod(groups = "unit")
  public void init() {
    this.toolSource = new InMemoryToolSource();
    this.toolStore = EasyMock.createMock(PersistentGalaxyToolStore.class);
    this.galaxyToolLoader = new GalaxyToolLoader(toolSource, toolStore);
    persistedTools = Lists.newArrayList();
    EasyMock.expect(toolStore.list()).andStubReturn(persistedTools);
  }
  
  @Test(groups = "unit")
  public void testCreate() {
    final Tool tool1 = registerTool(newToolWithId("testid"));
    expectCreateToolFor(tool1);
    replayUpdateAndVerify();
  }
  
  @Test(groups = "unit")
  public void testUpdate() {
    final Tool tool1 = registerAndPersistTool(newToolWithId("testid2"));
    toolStore.updateIfNeeded(persistedTools.get(0), GalaxyXmlUtils.serialize(tool1));
    EasyMock.expectLastCall();
    replayUpdateAndVerify();
  }
  
  private void replayUpdateAndVerify() {
    EasyMock.replay(toolStore);
    galaxyToolLoader.updatePersistentGalaxyToolStore();
    EasyMock.verify(toolStore);
  }
  
  
  private void expectCreateToolFor(final Tool tool) {    
    Map<String, Object> properties = 
      ImmutableMap.<String, Object>builder().
        put("name", tool.getName()).
        put("description", tool.getDescription()).
        build();
    final GalaxyTool persistedTool = new GalaxyTool();
    EasyMock.expect(toolStore.create(EasyMockUtils.<GalaxyTool>isBeanWithProperties(properties), 
                                     EasyMock.eq(GalaxyXmlUtils.serialize(tool)))).andReturn(persistedTool);
  }
  
  private Tool registerAndPersistTool(final Tool tool) {
    registerTool(tool);
    final GalaxyTool persistedGalaxyTool = new GalaxyTool();
    persistedTools.add(persistedGalaxyTool);
    String toolPersistenceId = UUID.randomUUID().toString();
    persistedGalaxyTool.setId(toolPersistenceId);    
    EasyMock.expect(toolStore.getXml(toolPersistenceId)).andStubReturn(GalaxyXmlUtils.serialize(tool));
    return tool;
  }
  
  private Tool registerTool(final Tool tool) {
    this.toolSource.addTool(tool);
    return tool;
  }
  
  private Tool newToolWithId(final String id) {
    final Tool tool = new Tool();
    tool.setName(UUID.randomUUID().toString());
    tool.setDescription(UUID.randomUUID().toString());
    tool.setId(id);
    return tool;
  }
  
}

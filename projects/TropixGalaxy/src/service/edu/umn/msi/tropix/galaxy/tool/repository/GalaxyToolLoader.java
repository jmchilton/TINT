package edu.umn.msi.tropix.galaxy.tool.repository;

import java.util.Map;

import javax.annotation.ManagedBean;
import javax.inject.Inject;

import com.google.common.collect.ImmutableMap;

import edu.umn.msi.tropix.galaxy.tool.Tool;
import edu.umn.msi.tropix.galaxy.xml.GalaxyXmlUtils;
import edu.umn.msi.tropix.models.GalaxyTool;
import edu.umn.msi.tropix.persistence.galaxy.PersistentGalaxyToolStore;

@ManagedBean
public class GalaxyToolLoader {
  private final ToolSource toolSource;
  private final PersistentGalaxyToolStore toolStore;
  
  @Inject
  public GalaxyToolLoader(final ToolSource toolSource,
                          final PersistentGalaxyToolStore toolStore) {
    this.toolSource = toolSource;
    this.toolStore = toolStore;
  }
  
  private Map<String, GalaxyTool> idToToolMap() {
    final ImmutableMap.Builder<String, GalaxyTool> builder = ImmutableMap.builder();
    for(final GalaxyTool galaxyTool : toolStore.list()) {
      final Tool tool = loadPersistedTool(galaxyTool);
      builder.put(tool.getId(), galaxyTool);
    }
    return builder.build();
  }

  private Tool loadPersistedTool(final GalaxyTool galaxyTool) {
    final String xml = toolStore.getXml(galaxyTool.getId());
    final Tool tool = GalaxyXmlUtils.deserialize(xml);
    return tool;
  }
    
  public void updatePersistentGalaxyToolStore() {
    final Map<String, GalaxyTool> idToToolMap = idToToolMap();
    for(final Tool tool : toolSource.getTools()) {
      final String toolXml = GalaxyXmlUtils.serialize(tool);
      // System.out.println(toolXml);
      final GalaxyTool galaxyTool = idToToolMap.get(tool.getId());
      if(galaxyTool == null) {
        final GalaxyTool newTool = new GalaxyTool();
        newTool.setName(tool.getName());
        newTool.setDescription(tool.getDescription());
        toolStore.create(newTool, toolXml);
      } else {
        toolStore.updateIfNeeded(galaxyTool, toolXml);
      }
    }
  }

}

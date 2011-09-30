package edu.umn.msi.tropix.galaxy.tool.repository;

import java.util.Map;

import javax.annotation.ManagedBean;
import javax.inject.Inject;

import org.springframework.jmx.export.annotation.ManagedOperation;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import edu.umn.msi.tropix.galaxy.tool.Tool;
import edu.umn.msi.tropix.galaxy.xml.GalaxyXmlUtils;
import edu.umn.msi.tropix.models.GalaxyTool;
import edu.umn.msi.tropix.persistence.galaxy.PersistentGalaxyToolStore;

@ManagedBean
public class GalaxyToolRepositoryImpl implements GalaxyToolRepository {
  private final PersistentGalaxyToolStore persistentStore;
  private final GalaxyToolLoader galaxyToolLoader;
  private Map<String, String> toolToPersistenceIdMap = Maps.newHashMap();
  
  @Inject
  public GalaxyToolRepositoryImpl(final PersistentGalaxyToolStore persistentStore,
                                  final GalaxyToolLoader galaxyToolLoader) {
    this.persistentStore = persistentStore;
    this.galaxyToolLoader = galaxyToolLoader;
    cacheToolMap();
  }

  public Tool loadForToolId(final String toolId) {
    return loadForPersistenceId(toolToPersistenceIdMap.get(toolId));
  }
  
  public Tool loadForPersistenceId(final String persistedId) {
    final Tool tool = GalaxyXmlUtils.deserialize(persistentStore.getXml(persistedId));
    return tool;
  }
  
  @ManagedOperation
  public void cacheToolMap() {
    galaxyToolLoader.updatePersistentGalaxyToolStore();
    final Iterable<GalaxyTool> persistedTools =  persistentStore.list();
    final ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
    for(final GalaxyTool persistedTool : persistedTools) {
      final String persistenceId = persistedTool.getId();
      final Tool tool = loadForPersistenceId(persistenceId);
      final String toolId = tool.getId();
      builder.put(toolId, persistenceId);
    }
    toolToPersistenceIdMap = builder.build();
  }
  
  
}

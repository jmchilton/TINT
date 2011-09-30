package edu.umn.msi.tropix.persistence.galaxy;

import java.util.List;

import edu.umn.msi.tropix.models.GalaxyTool;

public interface PersistentGalaxyToolStore {

  GalaxyTool create(GalaxyTool galaxyTool, String xml);

  void updateIfNeeded(final GalaxyTool galaxyTool, final String updatedXml);

  void update(GalaxyTool galaxyTool, String updatedXml);

  String getXml(String toolId);

  List<GalaxyTool> list();

  GalaxyTool load(String toolId);

}

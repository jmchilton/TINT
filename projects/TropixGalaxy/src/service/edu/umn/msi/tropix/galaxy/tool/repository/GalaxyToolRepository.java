package edu.umn.msi.tropix.galaxy.tool.repository;

import edu.umn.msi.tropix.galaxy.tool.Tool;

public interface GalaxyToolRepository {

  Tool loadForToolId(final String toolId);

  Tool loadForPersistenceId(final String persistedId);

}
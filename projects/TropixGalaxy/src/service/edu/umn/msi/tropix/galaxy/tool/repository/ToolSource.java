package edu.umn.msi.tropix.galaxy.tool.repository;

import edu.umn.msi.tropix.galaxy.tool.Tool;

public interface ToolSource {
  
  Iterable<Tool> getTools();
  
}

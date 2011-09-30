package edu.umn.msi.tropix.webgui.server;

import java.util.Map;

public interface ParameterLoader {

  Map<String, String> loadParameterMap(final String id);

}
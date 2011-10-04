package edu.umn.msi.tropix.webgui.client.constants;

public class DomConstants {
  public static String buildConstant(final String prefix, final String suffix) {
    return prefix + "_" + suffix;
  }

  public static final String MENU_ID_PREFIX = "Menu";
  public static final String FILE_MENU_ID = buildConstant(MENU_ID_PREFIX, "File");
  public static final String EXPORT_MENU_ID = buildConstant(MENU_ID_PREFIX, "Export");
  public static final String HELP_MENU_ID = buildConstant(MENU_ID_PREFIX, "Help");
  public static final String ADMIN_MENU_ID = buildConstant(MENU_ID_PREFIX, "Admin");
  public static final String PROJECT_MENU_ID = buildConstant(MENU_ID_PREFIX, "Project");
  public static final String SEARCH_MENU_ID = buildConstant(MENU_ID_PREFIX, "Search");
  public static final String TOOLS_ADMIN_MENU_ID = buildConstant(MENU_ID_PREFIX, "Tools");
}

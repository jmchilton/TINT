package edu.umn.msi.tropix.galaxy.tool.repository;

import java.io.InputStream;
import java.util.List;

public class ClassPathToolSourceImpl extends PathToolSourceImpl {
  private List<String> toolXmlPaths;
  
  public ClassPathToolSourceImpl(final List<String> toolXmlPaths) {
    this.toolXmlPaths = toolXmlPaths;
  }

  protected List<String> getToolXmlPaths() {
    return toolXmlPaths;
  }

  protected InputStream getWrapperInputStream(final String path) {    
    return getClass().getResourceAsStream(path);
  }

  protected InputStream getTestDataInputStream(final String path) {
    return getWrapperInputStream(path);
  }

}

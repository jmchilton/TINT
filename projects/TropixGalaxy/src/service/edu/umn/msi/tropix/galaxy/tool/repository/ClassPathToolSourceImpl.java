package edu.umn.msi.tropix.galaxy.tool.repository;

import java.io.InputStream;
import java.util.List;

public class ClassPathToolSourceImpl extends PathToolSourceImpl {
  private List<String> toolXmlPaths;
  private Class<?> resourceClass;
  
  public ClassPathToolSourceImpl(final List<String> toolXmlPaths) {
    this(toolXmlPaths, ClassPathToolSourceImpl.class);    
  }

  public ClassPathToolSourceImpl(final List<String> toolXmlPaths, final Class<?> resourceClass) {
    this.toolXmlPaths = toolXmlPaths;
    this.resourceClass = resourceClass;
  }

  protected List<String> getToolXmlPaths() {
    return toolXmlPaths;
  }

  protected InputStream getWrapperInputStream(final String path) {
    return resourceClass.getResourceAsStream(path);
  }

  protected InputStream getTestDataInputStream(final String path) {
    return getWrapperInputStream(path);
  }

}

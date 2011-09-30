package edu.umn.msi.tropix.common.data;

import java.io.File;
import java.io.InputStream;

import com.google.common.base.Preconditions;

import edu.umn.msi.tropix.common.io.FileUtils;
import edu.umn.msi.tropix.common.io.FileUtilsFactory;
import edu.umn.msi.tropix.common.io.InputContext;
import edu.umn.msi.tropix.common.io.InputContexts;

class RepositoryImpl implements Repository {
  private static final FileUtils FILE_UTILS = FileUtilsFactory.getInstance();
  private final String repositoryDirectory;
  private final String remoteRepositoryUri;
  
  public RepositoryImpl(final String repositoryDirectory, final String remoteRepositoryUri) {
    this.repositoryDirectory = repositoryDirectory;
    this.remoteRepositoryUri = remoteRepositoryUri;
  }
  
  public InputStream getResource(final Class<?> clazz, final String resourceName) {
    return getResource(clazz.getPackage().getName(), resourceName);
  }
  
  private File cacheResourceIfNeeded(final String packageName, final String resourceName) {
    final File packageRepositoryDirectory = new File(repositoryDirectory, packageName);
    final File resourceDestination = new File(packageRepositoryDirectory, resourceName);
    if(!resourceDestination.exists()) {
      packageRepositoryDirectory.mkdirs();
      Preconditions.checkState(packageRepositoryDirectory.isDirectory(), "Failed to create directory - " + packageRepositoryDirectory.getAbsolutePath() + " - for downloading tropix data files to.");        
      final String url = String.format("%s/%s/%s", remoteRepositoryUri, packageName, resourceName);
      final InputContext inputContext = InputContexts.forUrl(url);
      inputContext.get(resourceDestination);
    }
    return resourceDestination;
  }
  
  public InputStream getResource(final String packageName, final String resourceName) {
    final File resourceDestination = cacheResourceIfNeeded(packageName, resourceName);
    return FILE_UTILS.getFileInputStream(resourceDestination);
  }

  public InputContext getResourceContext(final Class<?> clazz, final String resourceName) {
    return getResourceContext(clazz.getPackage().getName(), resourceName);
  }

  public InputContext getResourceContext(final String packageName, final String resourceName) {
    final File resourceDestination = cacheResourceIfNeeded(packageName, resourceName);
    return InputContexts.forFile(resourceDestination);
  }

}

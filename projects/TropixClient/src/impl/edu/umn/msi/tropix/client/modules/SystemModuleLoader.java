package edu.umn.msi.tropix.client.modules;

import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.common.collect.ImmutableList;

import edu.umn.msi.tropix.models.modules.SystemModule;
import edu.umn.msi.tropix.models.utils.StockFileExtensionI;
import edu.umn.msi.tropix.persistence.service.impl.FileTypeResolver;

@ManagedBean
public class SystemModuleLoader {
  private ImmutableList<SystemModule> modules;
  private FileTypeResolver fileTypeResolver;

  @Inject
  public SystemModuleLoader(@Named("modulesIterable") final Iterable<String> modules, 
                            final FileTypeResolver fileTypeResolver) {
    this.fileTypeResolver = fileTypeResolver;
    final ImmutableList.Builder<SystemModule> modulesBuilder = ImmutableList.builder();
    for(final String module : modules) {
      modulesBuilder.add(SystemModule.valueOf(module));
    }
    this.modules = modulesBuilder.build();
  }
  
  @PostConstruct
  public void init() {
    for(final SystemModule systemModule : modules) {
      for(final StockFileExtensionI extension : systemModule.getFileTypes()) {
        fileTypeResolver.resolveType(extension);
      }
    }
  }

}

package edu.umn.msi.tropix.webgui.server.session;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

import edu.umn.msi.tropix.webgui.services.session.Module;
import edu.umn.msi.tropix.webgui.services.session.SessionInfo;

@ManagedBean
public class ModuleSessionInfoClosureImpl implements SessionInfoClosure {
  private final Iterable<Module> modules;

  @Inject
  public ModuleSessionInfoClosureImpl(@Named("modulesIterable") final Iterable<String> modules) {
    this.modules = Iterables.transform(modules, new Function<String, Module>() {
      public Module apply(final String moduleStr) {
        return Module.valueOf(moduleStr);
      }
    });
  }

  public void apply(final SessionInfo sessionInfo) {
    Iterables.addAll(sessionInfo.getModules(), modules);
  }

}

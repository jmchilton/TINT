package edu.umn.msi.tropix.webgui.server.session;

import java.util.List;

import javax.annotation.ManagedBean;
import javax.inject.Named;

import com.google.common.collect.Lists;

import edu.umn.msi.tropix.common.collect.Closure;
import edu.umn.msi.tropix.common.collect.Closures;
import edu.umn.msi.tropix.common.spring.TypedBeanProcessor;
import edu.umn.msi.tropix.webgui.services.session.SessionInfo;

@ManagedBean
@Named("sessionInfoSupplier")
public class SessionInfoSupplierImpl extends TypedBeanProcessor implements SessionInfoSupplier {
  private List<Closure<? super SessionInfo>> sessionInfoClosures = Lists.newArrayList();

  public SessionInfoSupplierImpl() {
    super(SessionInfoClosure.class);
  }

  /**
   * Once spring is inialized, this will be called with all SessionInfoClosure instances.
   */
  protected void processBeans(final Iterable<Object> beans) {
    for(final Object bean : beans) {
      final SessionInfoClosure closure = (SessionInfoClosure) bean;
      sessionInfoClosures.add(closure);
    }
  }

  private void populateSessionInfo(final SessionInfo sessionInfo) {
    Closures.compose(sessionInfoClosures).apply(sessionInfo);
  }

  public SessionInfo get() {
    final SessionInfo sessionInfo = new SessionInfo();
    populateSessionInfo(sessionInfo);
    return sessionInfo;
  }

}

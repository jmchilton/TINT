package edu.umn.msi.tropix.ssh;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AutoStartingSshServer {
  private static final Log LOG = LogFactory.getLog(AutoStartingSshServer.class);
  private final SshServerWrapper sshServer;

  @Inject
  public AutoStartingSshServer(final SshServerWrapper sshServer) {
    LOG.debug("Constructing AutoStartingSshServer");
    this.sshServer = sshServer;
  }
  
  @PostConstruct
  public void start() {
    LOG.debug("In postconstruct start method - going to start ssh server");
    this.sshServer.start();
  }
  
  @PreDestroy
  public void stop() {
    LOG.debug("In predestroy stop method - going to stop ssh server");
    this.sshServer.stop();    
  }

}

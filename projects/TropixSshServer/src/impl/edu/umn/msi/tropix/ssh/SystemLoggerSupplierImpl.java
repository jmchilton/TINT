package edu.umn.msi.tropix.ssh;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.productivity.java.syslog4j.Syslog;
import org.productivity.java.syslog4j.SyslogConfigIF;
import org.productivity.java.syslog4j.SyslogIF;
import org.productivity.java.syslog4j.impl.net.udp.UDPNetSyslogConfig;
import org.springframework.beans.factory.annotation.Value;

import com.google.common.base.Supplier;

public class SystemLoggerSupplierImpl implements Supplier<SyslogIF> {
  private boolean enableSyslog;
  private String protocol = "authudp";
  private String facility = "auth";
  private String host = "0.0.0.0";

  @Inject
  public void setEnableSyslog(@Value("#{${ssh.use.syslog}?:false}") final boolean enableSyslog) {
    this.enableSyslog = enableSyslog;
  }

  @Nullable
  public SyslogIF get() {
    SyslogIF instance = null;
    if(enableSyslog) {
      SyslogConfigIF config = new UDPNetSyslogConfig();
      config.setHost(host);
      config.setFacility(facility);
      instance = Syslog.createInstance(protocol, config);
    }
    return instance;
  }

}

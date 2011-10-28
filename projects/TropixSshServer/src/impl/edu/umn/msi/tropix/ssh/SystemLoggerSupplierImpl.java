package edu.umn.msi.tropix.ssh;

import javax.annotation.Nullable;

import org.productivity.java.syslog4j.Syslog;
import org.productivity.java.syslog4j.SyslogConfigIF;
import org.productivity.java.syslog4j.SyslogIF;
import org.productivity.java.syslog4j.impl.net.udp.UDPNetSyslogConfig;

import com.google.common.base.Supplier;

public class SystemLoggerSupplierImpl implements Supplier<SyslogIF> {
  private boolean enableSyslog;
  private String protocol = "udp";
  private String facility = "auth";
  
  public void setEnableSyslog(final boolean enableSyslog) {
    this.enableSyslog = enableSyslog;
  }

  @Nullable
  public SyslogIF get() {
    SyslogIF instance = null;
    if(enableSyslog) {
      SyslogConfigIF config = new UDPNetSyslogConfig();
      config.setHost("0.0.0.0");
      config.setFacility("auth");
      instance = Syslog.createInstance("authudp", config);
    }
    return instance;
  }

}

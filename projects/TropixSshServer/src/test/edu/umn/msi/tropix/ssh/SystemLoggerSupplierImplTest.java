package edu.umn.msi.tropix.ssh;

import org.productivity.java.syslog4j.SyslogIF;
import org.testng.annotations.Test;

public class SystemLoggerSupplierImplTest {
  
  @Test(groups = "unit")
  public void testProtocol() {
    final SystemLoggerSupplierImpl supplier = new SystemLoggerSupplierImpl();
    supplier.setEnableSyslog(true);
    final SyslogIF instance = supplier.get();
    
    assert instance != null;
    //assert instance.getProtocol().equals("udp");
    
    instance.warn("auth sshd[12345]: Failed keyboard-interactive for user ssh2");
  }
  
}

package edu.umn.msi.tropix.cloud.forwarding;

import java.io.ByteArrayOutputStream;
import java.util.List;

import javax.annotation.ManagedBean;

import com.google.common.collect.Lists;

import edu.umn.msi.tropix.common.execution.ExecutionConfiguration;
import edu.umn.msi.tropix.common.execution.ExecutionFactories;
import edu.umn.msi.tropix.common.execution.ExecutionFactory;
import edu.umn.msi.tropix.common.execution.ExecutionHook;

@ManagedBean
/**
 * In order to enable this to work properly, your sudoers file should be updated
 * to allow the user this Java process runs as to access to iptables without a 
 * password.
 * 
 * For an Ubuntu Tomcat 6 instance, adding the following lines to /etc/sudoers with
 * the command "sudo visudo" sets this up.
 * 
 * tomcat6    ALL=(ALL) NOPASSWD: /sbin/iptables
 */
public class IptablesExecutorImpl implements IptablesExecutor {
  public static final String DEFAULT_SUDO_COMMAND = "sudo";
  public static final String DEFAULT_IPTABLES_PATH = "/sbin/iptables";
  private String sudoCommand = DEFAULT_SUDO_COMMAND;
  private String iptablesPath = DEFAULT_IPTABLES_PATH;
  private ExecutionFactory executionFactory = ExecutionFactories.getDefaultExecutionFactory();
  
  public void executeWithArguments(final String[] iptablesArguments) {
    final ExecutionConfiguration configuration = new ExecutionConfiguration();
    configuration.setApplication(sudoCommand);
    
    
    final List<String> sudoArgs = Lists.newArrayList();
    sudoArgs.add(iptablesPath);
    sudoArgs.addAll(Lists.newArrayList(iptablesArguments));
    configuration.setArguments(sudoArgs);

    final ByteArrayOutputStream standardErrorStream = new ByteArrayOutputStream();
    configuration.setStandardErrorStream(standardErrorStream);
    
    final ExecutionHook executionHook = executionFactory.execute(configuration, null);
    executionHook.waitFor();  
    if(executionHook.getReturnValue() != 0) {
      try {
        Thread.sleep(1000);
      } catch(InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      System.out.println(new String(standardErrorStream.toByteArray()));
      
      throw new RuntimeException("Failed to redirect port");
      
    }    
  }  
  
  public void setExecutionFactory(final ExecutionFactory executionFactory) {
    this.executionFactory = executionFactory;
  }

}

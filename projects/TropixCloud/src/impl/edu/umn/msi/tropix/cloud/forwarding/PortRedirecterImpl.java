package edu.umn.msi.tropix.cloud.forwarding;

import javax.annotation.ManagedBean;
import javax.inject.Inject;

/**
 * In order to redirect, need to also accept connection. Something like this 
 * should be in place.
 * 
 * iptables -A INPUT -p tcp -m state --state NEW --dport 80 -i eth1 -j ACCEPT
 */
@ManagedBean
public class PortRedirecterImpl implements PortRedirecter {

  private final IptablesExecutor iptablesExecutor;

  @Inject
  public PortRedirecterImpl(final IptablesExecutor iptablesExecutor) {
    this.iptablesExecutor = iptablesExecutor;
  }
  
  public void redirect(final RedirectionInformation redirectionInformation) {
    String sourceArg = "";
    if(!redirectionInformation.getIncomingAddress().equals(ANY_ADDRESS)) {
      sourceArg = String.format("--source %s", redirectionInformation.getIncomingAddress());
    }
    final String interfaceArg = ""; // -i eth0
    final String argumentTemplate = "-A PREROUTING -t nat %s -p tcp %s --dport %d -j DNAT --to %s:%d";
    final String arguments = String.format(argumentTemplate, interfaceArg, sourceArg, 
        redirectionInformation.getIncomingPort(), redirectionInformation.getOutgoingAddress(), redirectionInformation.getOutgoingPort());
    System.out.println(arguments);
    iptablesExecutor.executeWithArguments(arguments.split("\\s+"));
  }

}

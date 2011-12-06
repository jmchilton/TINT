package edu.umn.msi.tropix.cloud.forwarding;

public interface IptablesExecutor {

  void executeWithArguments(final String[] iptablesArguments);
  
}

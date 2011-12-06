package edu.umn.msi.tropix.cloud.forwarding;

public interface PortRedirecter {
  String ANY_ADDRESS = "*";
  int ANY_PORT = -1; 
  void redirect(final RedirectionInformation redirectionInformation);
  
  public class RedirectionInformation {
    private String incomingAddress;
    private int incomingPort;
    private String outgoingAddress;
    private int outgoingPort;
    
    public String getIncomingAddress() {
      return incomingAddress;
    }

    public void setIncomingAddress(final String incomingAddress) {
      this.incomingAddress = incomingAddress;
    }
    
    public int getIncomingPort() {
      return incomingPort;
    }
    
    public void setIncomingPort(final int incomingPort) {
      this.incomingPort = incomingPort;
    }
    
    public String getOutgoingAddress() {
      return outgoingAddress;
    }
    
    public void setOutgoingAddress(final String outgoingAddress) {
      this.outgoingAddress = outgoingAddress;
    }
    
    public int getOutgoingPort() {
      return outgoingPort;
    }
    
    public void setOutgoingPort(final int outgoingPort) {
      this.outgoingPort = outgoingPort;
    }
    
  }

}
  
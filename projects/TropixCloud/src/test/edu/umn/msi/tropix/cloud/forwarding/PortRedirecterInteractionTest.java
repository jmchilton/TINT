package edu.umn.msi.tropix.cloud.forwarding;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.inject.Inject;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.cloud.forwarding.PortRedirecter.RedirectionInformation;

@ContextConfiguration("context.xml")
public class PortRedirecterInteractionTest extends AbstractTestNGSpringContextTests {

  // modprobe iptable_nat
  // echo 1 > /proc/sys/net/ipv4/ip_forward

  // Forward needs to accept
  // iptables -A FORWARD -p tcp -d 74.125.225.51 --dport 80 -m state --state NEW,ESTABLISHED,RELATED -j ACCEPT 
  // iptables -t nat -A POSTROUTING -j MASQUERADE    
  
  @Inject
  private PortRedirecter portRedirecter;

  @Test(groups = "interaction")
  public void testSimpleRedirection() {
    final RedirectionInformation redirection = new RedirectionInformation();
    redirection.setIncomingPort(9004);
    redirection.setIncomingAddress(PortRedirecter.ANY_ADDRESS);
    redirection.setOutgoingPort(80);
    redirection.setOutgoingAddress(getIpAddressForHost("google.com"));
    portRedirecter.redirect(redirection);
  }
  
  private String getIpAddressForHost(final String hostname) {
    try {
      final InetAddress inetAddress = InetAddress.getByName("google.com");
      return inetAddress.getHostAddress();
    } catch(UnknownHostException e) {
      throw new RuntimeException(e);
    }
  }
  
}

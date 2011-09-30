import java.net.InetAddress;

public class Host {
  public static void main(String[] args) throws Exception {
    System.out.println("Java believes your IP address to be " + InetAddress.getLocalHost().getHostAddress());
  }
}
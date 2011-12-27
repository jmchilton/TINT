package edu.umn.msi.tropix.client.credential;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.Map;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import com.google.common.base.Optional;

/**
 * Ugly class that must use global state because of the awkward interface for using custom
 * ssl socket factories established by the Java's LDAP API. 
 * 
 * @author John Chilton (jmchilton at gmail dot com)
 *
 */
public class ConfiguredSslSocketFactory extends SocketFactory {
  private static ThreadLocal<String> customTruststorePath = new ThreadLocal<String>();
  
  public static void setTruststorePath(final String path) {
    customTruststorePath.set(path);
  }
  
  public static void setCustomSslSocketFactoryIfNeeded(final Map properties, final Optional<String> truststore) {
    if(truststore.isPresent()) {
      setTruststorePath(truststore.get());
      properties.put("java.naming.ldap.factory.socket", ConfiguredSslSocketFactory.class.getName());
    }
  }
  
  public static ConfiguredSslSocketFactory getDefault() {
    return new ConfiguredSslSocketFactory();
  }
  
  public ConfiguredSslSocketFactory() {
    delegate = getDelegate();
  }

  public static SocketFactory getDelegate() {
    try {
      final SSLContext context = SSLContext.getInstance("SSL");
      final TrustManagerFactory tmfactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
      final KeyStore ks = KeyStore.getInstance("JKS");
      ks.load(new FileInputStream(customTruststorePath.get()), null);
      tmfactory.init(ks);
      final TrustManager[] trustStores = tmfactory.getTrustManagers();
      context.init(null, trustStores, new SecureRandom());
      return context.getSocketFactory(); 
    } catch(Exception e) {
      throw new RuntimeException(e);
    }
  }

  private SocketFactory delegate;
  
  public Socket createSocket() throws IOException {
    return delegate.createSocket();
  }

  public Socket createSocket(final InetAddress address, final int port, final InetAddress localAddress, final int localPort) throws IOException {
    return delegate.createSocket(address, port, localAddress, localPort);
  }

  public Socket createSocket(final InetAddress host, final int port) throws IOException {
    return delegate.createSocket(host, port);
  }

  public Socket createSocket(final String host, final int port, final InetAddress localHost, final int localPort) throws IOException, UnknownHostException {
    return delegate.createSocket(host, port, localHost, localPort);
  }

  public Socket createSocket(final String host, final int port) throws IOException, UnknownHostException {
    return delegate.createSocket(host, port);
  }

}

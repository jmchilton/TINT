package edu.umn.msi.tropix.webgui.client.constants;

import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Properties;

import com.google.gwt.i18n.client.Constants;

public class ConstantProxies {

  private static final ConstantInvocationHandler HANDLER = new ConstantInvocationHandler();

  public static <T extends Constants> T getProxy(final Class<T> forClass) {
    @SuppressWarnings("unchecked")
    T proxy = (T) Proxy.newProxyInstance(forClass.getClassLoader(),
        new Class[] {forClass},
        HANDLER);
    return proxy;
  }

  private static class ConstantInvocationHandler implements InvocationHandler {

    public Object invoke(final Object proxy,
                         final Method method,
                         final Object[] args) throws Throwable {
      @SuppressWarnings("unchecked")
      final Class<? extends Constants> declaringClass =
          (Class<? extends Constants>) method.getDeclaringClass();
      final String propertiesFileName = declaringClass.getSimpleName() + ".properties";
      final InputStream inputStream = declaringClass.getResourceAsStream(propertiesFileName);
      final Properties properties = new Properties();
      properties.load(inputStream);
      return properties.get(method.getName());
    }

  }

}

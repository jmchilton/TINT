package edu.umn.msi.tropix.webgui.server.servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//http://seewah.blogspot.com/2009/02/gwt-tips-2-nocachejs-getting-cached-in.html
public class CacheControlFilter implements Filter {

  public void destroy() {
  }

  public void doFilter(final ServletRequest request,
                       final ServletResponse response,
                       final FilterChain filterChain) throws IOException, ServletException {
    HttpServletRequest httpRequest = (HttpServletRequest) request;
    String requestURI = httpRequest.getRequestURI();

    if(requestURI.contains(".nocache.")) {
      HttpServletResponse httpResponse = (HttpServletResponse) response;
      httpResponse.setHeader("Cache-control", "no-cache, no-store, must-revalidate");
    }

    filterChain.doFilter(request, response);
  }

  public void init(final FilterConfig filterConfig) {
  }

}

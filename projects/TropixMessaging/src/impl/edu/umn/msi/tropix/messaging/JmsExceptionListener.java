package edu.umn.msi.tropix.messaging;

import javax.inject.Named;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@Named("jmsExceptionListener")
public class JmsExceptionListener implements ExceptionListener {
  private static final Log LOG = LogFactory.getLog(JmsExceptionListener.class);
  
  public void onException(final JMSException e) {
    edu.umn.msi.tropix.common.logging.ExceptionUtils.logQuietly(LOG, e);
  }
  
}

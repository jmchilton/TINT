package edu.umn.msi.tropix.messaging;

import javax.annotation.ManagedBean;
import javax.inject.Inject;

import org.springframework.jms.core.JmsTemplate;

@ManagedBean
public class QueueSender {
  private final JmsTemplate jmsTemplate;

  @Inject
  public QueueSender(final JmsTemplate jmsTemplate) {
    this.jmsTemplate = jmsTemplate;
  }

  public void send(final String message) {
    jmsTemplate.convertAndSend("Queue.Name", message);
  }
}

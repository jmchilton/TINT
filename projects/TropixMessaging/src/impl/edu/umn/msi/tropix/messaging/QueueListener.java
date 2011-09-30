package edu.umn.msi.tropix.messaging;

import javax.annotation.ManagedBean;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;


@ManagedBean
public class QueueListener implements MessageListener {
  
  public void onMessage(final Message message) {
    System.out.println("Received message");
    if(message instanceof TextMessage) {
      final TextMessage textMessage = (TextMessage) message;
      try {
        System.out.println(textMessage.getText());
      } catch(final JMSException e) {
        e.printStackTrace();
      }
    }
  }
  
}

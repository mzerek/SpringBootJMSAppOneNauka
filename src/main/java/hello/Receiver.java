package hello;

import java.io.File;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.util.FileSystemUtils;

public class Receiver {

    /**
     * Get a copy of the application context
     */
    @Autowired
    ConfigurableApplicationContext context;
    static String mailboxDestinationOne = "mailbox-destination-one";
    
      /**
     * When you receive a message, print it out, then shut down the application.
     * Finally, clean up any ActiveMQ server stuff.
     * @throws InterruptedException 
     */
    public void receiveMessageTwo(String message) throws InterruptedException {
        System.out.println("Received <" + message + ">");
        //context.close();
        FileSystemUtils.deleteRecursively(new File("activemq-data"));
        
        Thread.sleep(5000);
        // Send a message
        MessageCreator messageCreator = new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
            	//session.createTopic("fajnyTwo");
                return session.createTextMessage("ping!");
            }
        };
        JmsTemplate jmsTemplate = context.getBean(JmsTemplate.class);
        System.out.println("Sending a first message.");    
        jmsTemplate.send(mailboxDestinationOne, messageCreator);
      }
    
}

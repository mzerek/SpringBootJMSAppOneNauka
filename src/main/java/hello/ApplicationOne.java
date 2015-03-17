
package hello;

import java.io.File;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TopicRequestor;
import javax.jms.TopicSession;

import org.apache.activemq.broker.region.Topic;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.jms.listener.SimpleMessageListenerContainer;
import org.springframework.jms.listener.adapter.MessageListenerAdapter;
import org.springframework.jms.support.destination.DynamicDestinationResolver;
import org.springframework.util.FileSystemUtils;


@Configuration
@EnableAutoConfiguration
public class ApplicationOne {

    static String mailboxDestinationOne = "mailbox-destination-one";
    static String mailboxDestinationTwo = "mailbox-destination-two";

    @Bean
    Receiver receiver() {
        return new Receiver();
    }

    @Bean
	MessageListenerAdapter adapter(Receiver receiver) {
		MessageListenerAdapter messageListener = new MessageListenerAdapter(receiver);
		messageListener.setDefaultResponseQueueName(mailboxDestinationTwo);
		//messageListener.setDefaultResponseTopicName("fajnyTwo");
		messageListener.setDefaultListenerMethod("receiveMessageTwo");
		return messageListener;
	}

    @Bean
    SimpleMessageListenerContainer container(MessageListenerAdapter messageListener,
                                             ConnectionFactory connectionFactory) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setMessageListener(messageListener);
        container.setConnectionFactory(connectionFactory);
        //container.setDurableSubscriptionName("fajnyTwo");
        container.setDestinationName(mailboxDestinationTwo);
        return container;
    }

    public static void main(String[] args) {
        // Clean out any ActiveMQ data from a previous run
        FileSystemUtils.deleteRecursively(new File("activemq-data"));

        // Launch the application
        ConfigurableApplicationContext context = SpringApplication.run(ApplicationOne.class, args);

        // Send a message
        MessageCreator messageCreator = new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
            	//session.createTopic("fajnyOne");
                return session.createTextMessage("ping!");
            }
        };
        JmsTemplate jmsTemplate = context.getBean(JmsTemplate.class);        
        System.out.println("Sending a new message.");    
        jmsTemplate.send(mailboxDestinationOne, messageCreator);        
       
    }

}

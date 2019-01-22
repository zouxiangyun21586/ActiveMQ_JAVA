package com.yr.activemq;

import javax.jms.Connection;  
import javax.jms.ConnectionFactory;  
import javax.jms.Destination;  
import javax.jms.MessageConsumer;  
import javax.jms.Session;  
import javax.jms.TextMessage;  
import org.apache.activemq.ActiveMQConnection;  
import org.apache.activemq.ActiveMQConnectionFactory;  
	   
/**
 * 接收端  
 * @author zxy
 *
 * 2018年6月9日 下午11:42:46
 *
 */
public class Receive {  
  
    public static void main(String[] args) {  
  
        // ConnectionFactory：连接工厂，JMS用它创建连接  
        ConnectionFactory connectionFactory;  
  
        // Connection：JMS客户端到JMS Provider的连接  
        Connection connection = null;  
  
        // Session：一个发送或接收消息的线程  
        Session session;  
  
        // Destination：消息的目的地;消息发送给谁.  
        Destination destination;  
  
        //消费者，消息接收者  
        MessageConsumer consumer;  
  
        connectionFactory = new ActiveMQConnectionFactory(  
                ActiveMQConnection.DEFAULT_USER,  
                ActiveMQConnection.DEFAULT_PASSWORD,  
                "failover:(tcp://192.168.1.236.:61617,tcp://192.168.1.237:61618)");  
        try {  
  
            //构造从工厂得到连接对象  
            connection =connectionFactory.createConnection();  
  
            //启动  
            connection.start();  
  
            //获取操作连接  
            session = connection.createSession(false,  
                    Session.AUTO_ACKNOWLEDGE);
            
            //获取session  
            destination = session.createQueue("kast");  
  
            consumer =session.createConsumer(destination);  
            while (true) {  
                //设置接收者接收消息的时间，为了便于测试，这里设定为100s  
                TextMessage message =(TextMessage) consumer.receive(10);  
                if (null != message) {  
                    System.out.println("收到消息" + message.getText());  
                } else {  
                    break;  
                }  
            }  
        } catch (Exception e) {  
            e.printStackTrace();  
        } finally {  
            try {  
                if (null != connection)  
            	connection.close();  
            } catch (Throwable ignore) {  
  
            }  
        }  
    }  
}

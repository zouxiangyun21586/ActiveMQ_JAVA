package com.yr.cluster;

import java.io.FileOutputStream;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import com.yr.zxy.Zxy;

/**
 * activemq集群消费者
 * 
 * @author liucong
 *
 * @date 2017年8月17日
 */
public class activemqClusterReceive {
	// 连接账号 没设用户名和密码这里为空,如果配置了消费者密码认证,这里就写配置的密码
	private String userName = "";
	// 连接密码
	private String password = "";
	// 集群连接地址
	private String brokerURL = "failover:(tcp://192.168.1.236:51511,tcp://192.168.1.237:51511,tcp://192.168.1.238:51511)";
	// connection的工厂
	private ConnectionFactory factory;
	// 连接对象
	private Connection connection;
	// 一个操作会话
	private Session session;
	// 目的地,其实就是连接到哪个队列,如果是点对点,那么它的实现是Queue,如果是订阅模式,那它的实现是Topic
	private Destination destination;
	// 消费者,就是接收数据的对象
	private MessageConsumer consumer;

	public static void main(String[] args) {
		activemqClusterReceive receive = new activemqClusterReceive();
		receive.start();
		receive.getTextMessage();
//		 receive.getObject();
		// receive.getMapMessage();
		// receive.getByteMessage();
	}

	public void start() {
		try {
			// 根据用户名,密码,url创建一个连接工厂
			factory = new ActiveMQConnectionFactory(userName, password, brokerURL);
			// 从工厂中获取一个连接
			connection = factory.createConnection();
			// 测试过这个步骤不写也是可以的,但是网上的各个文档都写了
			connection.start();
			// 创建一个session
			// 第一个参数:是否支持事务,如果为true,则会忽略第二个参数,被jms服务器设置为SESSION_TRANSACTED
			// 第二个参数为false时,paramB的值可为Session.AUTO_ACKNOWLEDGE,Session.CLIENT_ACKNOWLEDGE,DUPS_OK_ACKNOWLEDGE其中一个。
			// Session.AUTO_ACKNOWLEDGE为自动确认,客户端发送和接收消息不需要做额外的工作。哪怕是接收端发生异常,也会被当作正常发送成功。
			// Session.CLIENT_ACKNOWLEDGE为客户端确认。客户端接收到消息后,必须调用javax.jms.Message的acknowledge方法。jms服务器才会当作发送成功,并删除消息。
			// DUPS_OK_ACKNOWLEDGE允许副本的确认模式。一旦接收方应用程序的方法调用从处理消息处返回,会话对象就会确认消息的接收；而且允许重复确认。
//			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			session = connection.createSession(true, Session.SESSION_TRANSACTED);//true表示开启事物
			// 创建一个到达的目的地,其实想一下就知道了,activemq不可能同时只能跑一个队列吧,这里就是连接了一个名为"text-msg"的队列,这个会话将会到这个队列,当然,如果这个队列不存在,将会被创建
			destination = session.createQueue("em");
			// 根据session,创建一个接收者对象
			consumer = session.createConsumer(destination);

		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 得到txt的消息
	 */
	public void getTextMessage() {
		try {
			// 实现一个消息的监听器
			// 实现这个监听器后,以后只要有消息,就会通过这个监听器接收到
			consumer.setMessageListener(new MessageListener() {
				public void onMessage(Message message) {
					try {
//						Thread.sleep(100);
						// 获取到接收的数据
						String text = ((TextMessage) message).getText();
						System.out.println("接受消息:" + text);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			// 关闭接收端,也不会终止程序哦
			// consumer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 得到对象数据
	 */
	public void getObject() {
		try {
			// 实现一个消息的监听器
			// 实现这个监听器后,以后只要有消息,就会通过这个监听器接收到
			consumer.setMessageListener(new MessageListener() {
				public void onMessage(Message message) {
					try {
						// 获取到接收的数据
						Zxy zxy = (Zxy) ((ObjectMessage) message).getObject();
						System.out.println("接受消息:" + zxy.toString());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			// 关闭接收端,也不会终止程序哦
			// consumer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 接收Map类型的数据
	 */
	public void getMapMessage() {
		try {
			// 实现一个消息的监听器
			// 实现这个监听器后,以后只要有消息,就会通过这个监听器接收到
			consumer.setMessageListener(new MessageListener() {
				public void onMessage(Message message) {
					try {
						// 获取到接收的数据
						MapMessage map = (MapMessage) message;
						String name = map.getString("name");
						Integer age = map.getInt("age");
						System.out.println("姓名:" + name + "  年龄:" + age);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			// 关闭接收端,也不会终止程序哦
			// consumer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 得到Byte类型的数据
	 */
	public void getByteMessage() {
		try {
			// 实现一个消息的监听器
			// 实现这个监听器后,以后只要有消息,就会通过这个监听器接收到
			consumer.setMessageListener(new MessageListener() {
				public void onMessage(Message message) {
					FileOutputStream out = null;
					try {
						// 获取到接收的数据
						BytesMessage bytesMessage = (BytesMessage) message;
						out = new FileOutputStream("d:/a.txt");
						byte[] byt = new byte[1024];
						int len = 0;
						while ((len = bytesMessage.readBytes(byt)) != -1) {
							out.write(byt, 0, len);
						}
						out.close();
						System.out.println("写入文件成功");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
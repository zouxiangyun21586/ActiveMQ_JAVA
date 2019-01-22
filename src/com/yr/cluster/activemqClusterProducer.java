package com.yr.cluster;
import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.StreamMessage;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

import com.yr.zxy.Zxy;

/**
 * activemq集群生产者
 * 
 * @author liucong
 *
 * @date 2017年8月10日
 */
public class activemqClusterProducer {

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
	// 生产者,就是产生数据的对象
	private MessageProducer producer;

	public static void main(String[] args) {
		activemqClusterProducer send = new activemqClusterProducer();
		send.start();
		send.sendTextMessage();
//		 send.sendObjMessage();
		// send.sendMapMessage();
		// send.sendByteMessage();
		// send.sendStreamMessage();
	}

	public void start() {
		try {
			// 根据用户名,密码,url创建一个连接工厂
			factory = new ActiveMQConnectionFactory(userName, password, brokerURL);
			// 从工厂中获取一个连接
			connection = factory.createConnection();
			// 测试过这个步骤不写也是可以的,但是网上的各个文档都写了 connection.start();
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
			// 从session中,获取一个消息生产者
			producer = session.createProducer(destination);
			// 设置生产者的模式,有两种可选
			// DeliveryMode.PERSISTENT 当activemq关闭的时候,队列数据将会被保存
			// DeliveryMode.NON_PERSISTENT 当activemq关闭的时候,队列里面的数据将会被清空
			// producer.setDeliveryMode(DeliveryMode.PERSISTENT);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 发送text消息
	 */
	public void sendTextMessage() {
		try {
			// 创建一条消息,当然,消息的类型有很多,如文字,字节,对象等,可以通过session.create..方法来创建出来
			TextMessage textMsg = session.createTextMessage();
			for (int i = 0; i < 20; i++) {
				textMsg.setText("接收消息:" + i);
				producer.send(textMsg);// 发送一条消息
			}
			System.out.println("发送消息成功");
			session.commit();// 提交,如果未提交,消息中间件是没有消息的,消费者也读取不到
			// 即便生产者的对象关闭了,程序还在运行哦 producer.close();
		} catch (Exception e) {
			e.printStackTrace();
			try {
				session.rollback(); // 出错了就回滚
			} catch (JMSException e1) {
				e1.printStackTrace();
			}
		}
		try {
			session.close(); // 关闭
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 发送对象
	 */
	public void sendObjMessage() {
		try {
			for (int i = 1; i < 15; i++) {
				Zxy zxy = new Zxy();
				int age = 1 + i;
				zxy.setAge(age);
				zxy.setName("蘑菇 -- " + i);
				zxy.setAddr("深圳 - 大浪 - " + i + "区");
				ObjectMessage objectMessage = session.createObjectMessage();
				objectMessage.setObject(zxy);
//				System.out.println(111);
				producer.send(objectMessage);
//				System.out.println(222);
				session.commit();// 提交,如果未提交,消息中间件是没有消息的,消费者也读取不到
			}
			System.out.println("序列化对象发送成功");
		} catch (Exception e) {
			e.printStackTrace();
			try {
				session.rollback(); // 出错了就回滚
			} catch (JMSException e1) {
				e1.printStackTrace();
			}
		}
		try {
			session.close(); // 关闭
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 发送Map
	 */
	public void sendMapMessage() {
		try {
			MapMessage mapMessage = session.createMapMessage();
			mapMessage.setString("name", "邹想云");
			mapMessage.setInt("age", 18);

			producer.send(mapMessage);
			System.out.println("map消息发送成功");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 发送byte
	 */
	public void sendByteMessage() {
		try {
			byte byt[] = "我叹世事多变化,世事望我却依然".getBytes();
			BytesMessage bytesMessage = session.createBytesMessage();
			bytesMessage.writeBytes(byt);
			producer.send(bytesMessage);
			System.out.println("byte消息发送成功");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 发送stream
	 */
	public void sendStreamMessage() {
		try {
			StreamMessage streamMessage = session.createStreamMessage();
			streamMessage.writeString("Stream String");
			streamMessage.writeLong(17666129192L);
			producer.send(streamMessage);
			System.out.println("stream消息发送成功");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
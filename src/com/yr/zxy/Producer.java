package com.yr.zxy;

import java.io.ByteArrayOutputStream;
import java.io.File;

import javax.imageio.stream.FileImageInputStream;
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

/**
 * 对象与五种基本类型 -- 生产者
 * @author zxy
 *
 * 2018年6月7日 下午7:09:42
 *
 */
public class Producer {
	// 连接账号 没设用户名和密码这里为空,如果配置了消费者密码认证,这里就写配置的密码
	private String userName = "";
	// 连接密码
	private String password = "";
	// 连接地址
//	private String brokerURL = "failover:(tcp://192.168.1.237:61616)?initialReconnectDelay=1000";
	private String brokerURL = "tcp://192.168.1.238:61616";
	// connection的工厂
	private ConnectionFactory factory;
	// 连接对象
	private Connection connection;
	// 一个操作会话
	private static Session session;
	// 目的地，其实就是连接到哪个队列，如果是点对点，那么它的实现是Queue，如果是订阅模式，那它的实现是Topic
	private Destination destination;
	// 生产者，就是产生数据的对象
	private static MessageProducer producer;

	public static void main(String[] args) {
		Producer send = new Producer();
		send.start();
//		send.sendTextMessage(); // 存 String
//		send.sendObjMessage(); // 存 对象
		send.sendMapMessage(); // 存 Map
//		send.sendByteMessage(); // 存 Byte
//		send.sendStreamMessage(); // 存 流
//		send.sendMessage(session, producer);
	}

	public void start() {
		try {
			// 根据用户名，密码，url创建一个连接工厂
			factory = new ActiveMQConnectionFactory(userName, password, brokerURL);
			// 从工厂中获取一个连接
			connection = factory.createConnection();
			// 测试过这个步骤不写也是可以的，但是网上的各个文档都写了 connection.start();
			// 创建一个session
			// 第一个参数:是否支持事务，如果为true，则会忽略第二个参数，被jms服务器设置为SESSION_TRANSACTED
			// 第二个参数为false时，paramB的值可为Session.AUTO_ACKNOWLEDGE，Session.CLIENT_ACKNOWLEDGE，DUPS_OK_ACKNOWLEDGE其中一个。
			// Session.AUTO_ACKNOWLEDGE为自动确认，客户端发送和接收消息不需要做额外的工作。哪怕是接收端发生异常，也会被当作正常发送成功。
			// Session.CLIENT_ACKNOWLEDGE为客户端确认。客户端接收到消息后，必须调用javax.jms.Message的acknowledge方法。jms服务器才会当作发送成功，并删除消息。
			// DUPS_OK_ACKNOWLEDGE允许副本的确认模式。一旦接收方应用程序的方法调用从处理消息处返回，会话对象就会确认消息的接收；而且允许重复确认。
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			// 创建一个到达的目的地，其实想一下就知道了，activemq不可能同时只能跑一个队列吧，这里就是连接了一个名为"text-msg"的队列，这个会话将会到这个队列，当然，如果这个队列不存在，将会被创建
			destination = session.createQueue("zxy");
//			destination = session.createTopic("zxy"); //--- 
			// 从session中，获取一个消息生产者
			producer = session.createProducer(destination);
			// 设置生产者的模式，有两种可选
			// DeliveryMode.PERSISTENT 当activemq关闭的时候，队列数据将会被保存
			// DeliveryMode.NON_PERSISTENT 当activemq关闭的时候，队列里面的数据将会被清空
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
			// 创建一条消息，当然，消息的类型有很多，如文字，字节，对象等,可以通过session.create..方法来创建出来
			TextMessage textMsg = session.createTextMessage();
			for (int i = 0; i < 10; i++) {
				textMsg.setText("点对点(一对一)消息模式" + i);
				producer.send(textMsg);// 发送一条消息
			}
			System.out.println("发送消息成功");
			// 即便生产者的对象关闭了，程序还在运行哦 producer.close();
		} catch (Exception e) {
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
				producer.send(objectMessage);
			}
			System.out.println("序列化对象发送成功");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 发送Map
	 */
	public void sendMapMessage() {
		try {
			MapMessage mapMessage = session.createMapMessage();
			mapMessage.setString("name", "邹想云"); // key val 
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
			byte byt[] = "我多想再见你,哪怕匆匆一眼就别离".getBytes();
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
			
			StreamMessage msg1 = session.createStreamMessage();  
            msg1.writeBoolean(false);  
            msg1.writeLong(1234567890);  
            producer.send((StreamMessage) msg1);  
			
			System.out.println("stream消息发送成功");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 存入图片
	 * @author zxy
	 * 
	 * 2018年6月7日 下午8:36:56
	 * 
	 * @param session
	 * @param producer
	 */
	public void sendMessage(Session session, MessageProducer producer){
		try {
			BytesMessage streamMessage = session.createBytesMessage();
			byte[] data = null;
			FileImageInputStream input = null;
		    try {
		      input = new FileImageInputStream(new File("F:\\zxy\\my.png\\Innovation\\yy.jpg")); // 获取要上传的本地图片路径
		      ByteArrayOutputStream output = new ByteArrayOutputStream();
		      byte[] buf = new byte[10086]; // 可存入多大
		      int numBytesRead = 0;
		      while ((numBytesRead = input.read(buf)) != -1) { // 读取文件大小
		    	  output.write(buf, 0, numBytesRead); // 将指定字节数组中从偏移量 off(0) 开始的 len(numBytesRead) 个字节写入此字节数组输出流
		      }
		      data = output.toByteArray(); // 使用新的字节数组接住: 拷贝数组的大小和当前输出流的大小, 内容是的当前输出流
		      output.close();
		      input.close();
		    } catch (Exception ex1) {
		      ex1.printStackTrace();
		    }
		    streamMessage.writeBytes(data); // 生成图片
		    producer.send(streamMessage); // 消息生产者发出消息
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}

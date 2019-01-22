package aaa;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

/**
 * 生产者
 * 
 * @author liucong
 *
 * @date 2017年8月10日
 */
public class MQProducer {

	// 连接账号
	private String userName = "";
	// 连接密码
	private String password = "";
	// 连接地址
	private String brokerURL = "tcp://192.168.1.237:61616";
	// connection的工厂
	private ConnectionFactory factory;
	// 连接对象
	private Connection connection;
	// 一个操作会话
	private Session session;
	// 目的地，其实就是连接到哪个队列，如果是点对点，那么它的实现是Queue，如果是订阅模式，那它的实现是Topic
	private Destination destination;
	// 生产者，就是产生数据的对象
	private MessageProducer producer;

	public static void main(String[] args) {
		MQProducer send = new MQProducer();
		send.start();
		send.sendTextMessage();

	}

	/**
	 * Topic 点对点(一对一) 
	 * 		:总数与消费条数不一致
	 * Producer 发布订阅(一对多) -- 一 发布消息,多接收消息并且同时接收   
	 * 		:总数与消费条数一致
	 * 
	 * @author zxy
	 * 
	 * 2018年6月7日 下午5:14:37
	 *
	 */
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
			// Session.AUTO_ACKNOWLEDGE为自动确认，客户端发送和接收消息不需要做额外的工作。哪怕是接收端发生异常，也会被当作正常发送成功。 -- 会消费,显示消费人数,服务器关闭后没有消费人,有消费数,有总数
			// Session.CLIENT_ACKNOWLEDGE为客户端确认。客户端接收到消息后，必须调用javax.jms.Message的acknowledge方法。jms服务器才会当作发送成功，并删除消息。 -- 不会消费,会显示消费人数,服务器关闭后没有消费人,没有消费数,只有总数
			// DUPS_OK_ACKNOWLEDGE允许副本的确认模式。一旦接收方应用程序的方法调用从处理消息处返回，会话对象就会确认消息的接收；而且允许重复确认。 --- 不会消费,会显示消费人数,服务器关闭后没有消费数,没有消费人,只有总数
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			// 点对点与订阅模式唯一不同的地方，就是这一行代码，点对点创建的是Queue，而订阅模式创建的是Topic
			destination = session.createTopic("zxy"); // 点对点(一对一)
//			Queue queue = session.createQueue("zxy"); // 可用destination(目的地)接
			// 从session中，获取一个消息生产者
			producer = session.createProducer(destination);
//			producer = session.createProducer(queue);
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
			for (int i = 0; i < 20; i++) {
				textMsg.setText("cess" + i);
				producer.send(textMsg);// 发送一条消息
			}
			System.out.println("发布消息成功");
			// 即便生产者的对象关闭了，程序还在运行哦 producer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
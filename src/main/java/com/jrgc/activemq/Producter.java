package com.jrgc.activemq;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class Producter {

    //ActiveMQ 的链接地址
    private static final String DEFAULT_BROKER_URL ="tcp://10.105.6.222:61616";
    //队列名称
    private static final String QUEUE_NAME="yuQueue";


    AtomicInteger count = new AtomicInteger(0);
    //链接工厂
    ConnectionFactory connectionFactory;
    //链接对象
    Connection connection;
    //事务管理
    Session session;
    //缓存数据 MessageProducer
    ThreadLocal<MessageProducer> threadLocal = new ThreadLocal<MessageProducer>();
    //队列名称
    Queue queue;

    public void init(){
        try {
            //创建一个链接工厂
            connectionFactory = new ActiveMQConnectionFactory(DEFAULT_BROKER_URL);
            //从工厂中创建一个链接
            connection  = connectionFactory.createConnection();
            //开启链接
            connection.start();
            //创建一个事务（这里通过参数可以设置事务的级别）
            session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
            queue = session.createQueue(QUEUE_NAME);
            //trustedPackagesc
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(){
        try {
            //消息生产者
            MessageProducer messageProducer = null;
                if(threadLocal.get()!=null){
                    messageProducer = threadLocal.get();
            }else{
                messageProducer = session.createProducer(queue);
                threadLocal.set(messageProducer);
            }
            PrdOrder po =null;
            Random random =new Random();
            while(true){
                Thread.sleep(1000);
                int num = count.getAndIncrement();
                //创建一条消息 传入对象
                po =new PrdOrder(num,"No_"+num,"PrdOrder_"+num,random.nextDouble()*100000);

                TextMessage textMessage = session.createTextMessage();
                textMessage.setText(po.toString());
                System.out.println(Thread.currentThread().getName()+
                        "productor:"+po.toString());
                //发送消息
                messageProducer.send(textMessage);
                //提交事务
                session.commit();
            }
        } catch (JMSException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
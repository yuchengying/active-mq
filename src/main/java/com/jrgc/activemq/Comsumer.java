package com.jrgc.activemq;

import com.alibaba.fastjson.JSON;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Comsumer {

    //ActiveMQ 的链接地址
    private static final String DEFAULT_BROKER_URL ="tcp://10.105.6.222:61616";
    //队列名称
    private static final String QUEUE_NAME="yuQueue";

    ConnectionFactory connectionFactory;

    Connection connection;

    Session session;
    //队列名称
    Queue queue;

    ThreadLocal<MessageConsumer> threadLocal = new ThreadLocal<MessageConsumer>();
    AtomicInteger count = new AtomicInteger();

    public void init(){
        try {
            connectionFactory = new ActiveMQConnectionFactory(DEFAULT_BROKER_URL);
            connection  = connectionFactory.createConnection();
            connection.start();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            queue = session.createQueue(QUEUE_NAME);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }


    public void getMessage(){
        try {
            MessageConsumer consumer = null;
            if(threadLocal.get()!=null){
                consumer = threadLocal.get();
            }else{
                consumer = session.createConsumer(queue);
                threadLocal.set(consumer);
            }
            PrdOrder prdOrder=null;
            while(true){
                TextMessage  textMessage = (TextMessage) consumer.receive();
                if(textMessage!=null) {
                    textMessage.acknowledge();
                   String jsonStr = textMessage.getText();
                    PrdOrder po = JSON.parseObject(jsonStr, PrdOrder.class);
                    System.out.println(Thread.currentThread().getName()+": Consumer:"+po.getOrderName()+","+po.getOrderNo()+","+po.getPrice());
                }else {
                    break;
                }
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
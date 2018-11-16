package com.jrgc.mq;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 队列消费者
 *
 * @yuchengying
 * @create 2018-02-08 13:37
 **/

public class QueueConsumer extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //super.doGet(req, resp);
        doPost(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //super.doPost(req, resp);
        //获得jndi 接口
        QueueConnection queueConnection=null;
        try {
            InitialContext context = new InitialContext();
            //得到工厂类
            QueueConnectionFactory factory = (QueueConnectionFactory) context.lookup("java:comp/env/queue/connectionFactory");
            //得到 queue
            Queue queue = (Queue) context.lookup("java:comp/env/queue/myFirstQueue");
            //得到队列链接
            queueConnection = factory.createQueueConnection();
            //得到队列会话
            QueueSession queueSession =  queueConnection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);//自动应答
            //得到 consumer
            QueueReceiver queueReceiver = queueSession.createReceiver(queue);
            queueConnection.start();
            //得到 TextMessage 传输对象
            while(true){
                TextMessage  textMessage = (TextMessage) queueReceiver.receive();
                System.out.println("QueueConsumer receive :"+textMessage.getText());
            }
        } catch (NamingException e) {
            e.printStackTrace();
        } catch (JMSException e) {
            e.printStackTrace();
        }finally{
            if(queueConnection!=null){
                try {
                    queueConnection.close();
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        System.out.println("=======init QueueConsumer start=====");
    }
}

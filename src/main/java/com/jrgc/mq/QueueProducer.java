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
 * @yuchengying
 * @create 2018-02-08 11:23
 **/
public class QueueProducer extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //super.doGet(req, resp);
        doPost(req,resp);
    }

    /**
     * JNDI java mane and Dirextor interface java 命名接口规范
     * J2EE 要求所有的容器都实现 JNDI 的规范
     * activemq 以 ptp 点对点的模式、发布订阅者 模式来运行。
     * 1、queue 是 点对点的模式，对 时间差不敏感。不论是先起 身缠
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
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
           QueueSession queueSession =  queueConnection.createQueueSession(false, Session.DUPS_OK_ACKNOWLEDGE);
           //得到会话 sender
           QueueSender queueSender = queueSession.createSender(queue);

            queueConnection.start();

            //设置queueSender的 特性
            queueSender.setDeliveryMode(DeliveryMode.NON_PERSISTENT);//非持久化
           //得到 TextMessage 传输对象
            int i=0;
            TextMessage  textMessage = null;
            while(true){
                textMessage=queueSession.createTextMessage();
                String msg = "hello,yuchengying,num="+i++;
                textMessage.setText(msg);
                //通过queueSender 发送
                queueSender.send(textMessage);
                System.out.println("QueueProducer send :"+msg);
                Thread.sleep(2000);
            }
        } catch (NamingException e) {
            e.printStackTrace();
        } catch (JMSException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally{
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
        System.out.println("=======init QueueProducer start=====");
    }
}

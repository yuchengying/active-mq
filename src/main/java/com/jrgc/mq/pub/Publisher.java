package com.jrgc.mq.pub;

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
 * @create 2018-02-08 15:57
 **/

public class Publisher extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //super.doGet(req, resp);
        doPost(req,resp);
    }

    /**
     * 发布 订阅者 模式
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
       // super.doPost(req, resp);
        //获得jndi 接口
        TopicConnection topicConnection=null;
        try {
            InitialContext context = new InitialContext();
            //得到工厂类
            TopicConnectionFactory factory = (TopicConnectionFactory) context.lookup("java:comp/env/topic/connectionFactory");
            //得到 Topic
            Topic topic = (Topic) context.lookup("java:comp/env/topic/topic0");
            //得到队列链接
            topicConnection = factory.createTopicConnection();
            //得到队列会话
            TopicSession TopicSession =  topicConnection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
            //得到会话 sender
            TopicPublisher topicPublisher = TopicSession.createPublisher(topic);

            topicConnection.start();

            //设置TopicSender的 特性
            topicPublisher.setDeliveryMode(DeliveryMode.NON_PERSISTENT);//非持久化
            //得到 TextMessage 传输对象
            int i=0;
            TextMessage  textMessage = null;
            while(true){
                textMessage=TopicSession.createTextMessage();
                String msg = "hello,yuchengying,num="+i++;
                textMessage.setText(msg);
                //通过 topicPublisher 发送
                topicPublisher.publish(textMessage);
                System.out.println("TopicProducer publish :"+msg);
                Thread.sleep(2000);
            }
        } catch (NamingException e) {
            e.printStackTrace();
        } catch (JMSException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally{
            if(topicConnection!=null){
                try {
                    topicConnection.close();
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        System.out.println("=======Publisher init start=======");
    }
}

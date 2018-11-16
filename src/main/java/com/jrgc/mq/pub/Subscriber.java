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
 * @create 2018-02-08 16:06
 **/

public class Subscriber extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //super.doGet(req, resp);
        doPost(req,resp);
    }

    /**
     * topic模式 会广播给所有的订阅者，消息可以重复消费
     * @param req  有时间依赖性 之前发送的消息消费不掉。
     *             发布-订阅者模式 先启动订阅者，再启动发布者
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //super.doPost(req, resp);
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
            //得到会话 TopicSubscriber 订阅者
            TopicSubscriber topicSubscriber = TopicSession.createSubscriber(topic);

            topicConnection.start();

            //得到 TextMessage 传输对象
            TextMessage  textMessage = null;
            while(true){
                textMessage = (TextMessage) topicSubscriber.receive();
                System.out.println("TopicSubscriber receive :"+textMessage.getText());
            }
        } catch (NamingException e) {
            e.printStackTrace();
        } catch (JMSException e) {
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
        System.out.println("=======Subscriber init start=======");
    }
}

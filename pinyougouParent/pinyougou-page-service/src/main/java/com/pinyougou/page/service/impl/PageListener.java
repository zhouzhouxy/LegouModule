package com.pinyougou.page.service.impl;

import com.pinyougou.page.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 * 消息监听类
 * @author Administrator
 */
@Component
public class PageListener implements MessageListener {

    @Autowired
    private ItemPageService itemPageService;


    @Override
    public void onMessage(Message message) {
        TextMessage textMessage=(TextMessage)message;
        try {
            String text = textMessage.getText();
            System.out.println("接收到消息:"+text);

            boolean b = itemPageService.genItemHtml(Long.valueOf(text));
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}

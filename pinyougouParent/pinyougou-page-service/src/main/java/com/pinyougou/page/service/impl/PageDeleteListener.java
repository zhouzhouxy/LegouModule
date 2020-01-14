package com.pinyougou.page.service.impl;

import com.pinyougou.page.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

/**
 * current system date 2020/1/8/008
 *
 * @author asura
 */
public class PageDeleteListener implements MessageListener {

    @Autowired
    private ItemPageService itemPageService;

    @Override
    public void onMessage(Message message) {
        ObjectMessage objectMessage=(ObjectMessage)message;
        try {
           Long[] goodsIds= (Long[]) objectMessage.getObject();
            System.out.println("接收到消息"+goodsIds);
            boolean b = itemPageService.deleteItemHtml(goodsIds);
            System.out.println("删除网页"+b);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

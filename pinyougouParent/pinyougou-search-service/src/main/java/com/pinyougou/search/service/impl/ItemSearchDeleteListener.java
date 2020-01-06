package com.pinyougou.search.service.impl;

import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.util.Arrays;

/**
 * 监听：用于删除搜索库中记录
 * @author Administrator
 */
@Component
public class ItemSearchDeleteListener implements MessageListener {

    @Autowired
    private ItemSearchService itemSearchService;

    @Override
    public void onMessage(Message message) {
        ObjectMessage objectMessage =(ObjectMessage)message;
        try {
            Long[] goodsIds= (Long[]) objectMessage.getObject();
            System.out.println("ItemDeleteListener监听收到到消息"+goodsIds);
            itemSearchService.deleteByGoodsIds(Arrays.asList(goodsIds));
            System.out.println("成功删除索引库中的记录");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

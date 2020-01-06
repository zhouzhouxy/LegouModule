package com.pinyougou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.List;
import java.util.Map;

/**
 *
 * 监听类
 * @author Administrator
 */
@Component
public class ItemSearchListener implements MessageListener {
    @Autowired
    private ItemSearchService itemSearchService;

    @Override
    public void onMessage(Message message) {
        System.out.println("监听接收到消息");
        try {
            TextMessage textMessage=(TextMessage)message;
            String text=textMessage.getText();
            List<TbItem> list = JSON.parseArray(text, TbItem.class);
            for (TbItem item : list) {
                System.out.println(item.getId()+"  "+item.getTitle());
                //将spec字段中json字符串转换为map
                Map specMap = JSON.parseObject(item.getSpec());
                //给带注解的字段赋值
                item.setSpecMap(specMap);
            }
            itemSearchService.importList(list);
            System.out.println("成功导入到索引库");
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}

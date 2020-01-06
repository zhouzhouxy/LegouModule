package com.test.redis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value = "classpath:spring/applicationContext-redis.xml")
public class ListValue {

    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 右压栈，后添加的对象排在后边
     */
    @Test
    public void testSetValue1(){
        redisTemplate.boundListOps("namelist1").rightPush("刘备");
        redisTemplate.boundListOps("namelist1").rightPush("关羽");
        redisTemplate.boundListOps("namelist1").rightPush("张飞");
    }

    /**
     * 左压栈，后添加的对象排在前边
     */
    @Test
    public void testSetValue2(){
        redisTemplate.boundListOps("namelist2").leftPush("刘备");
        redisTemplate.boundListOps("namelist2").leftPush("关羽");
        redisTemplate.boundListOps("namelist2").leftPush("张飞");
    }
    @Test
    public void testGetValue1(){
        //显示右压栈集合
        List namelist1 = redisTemplate.boundListOps("namelist1").range(0, 10);
        System.out.println(namelist1);
        //显示左压栈集合中某个元素
        String namelist2 = (String)redisTemplate.boundListOps("namelist2").index(1);
        System.out.println(namelist2);
    }
    /**
     *移除集合中某个元素
     */
    @Test
    public void testsRemoveByIndex(){
        redisTemplate.boundListOps("namelist1").remove(1,"关羽");
    }

}

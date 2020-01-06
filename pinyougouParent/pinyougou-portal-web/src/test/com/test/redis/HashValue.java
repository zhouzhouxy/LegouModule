package com.test.redis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Set;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value="classpath:spring/applicationContext-redis.xml")
public class HashValue {
    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testSetValue(){
        redisTemplate.boundHashOps("namehash").put("a","唐僧");
        redisTemplate.boundHashOps("namehash").put("b","悟空");
        redisTemplate.boundHashOps("namehash").put("c","八戒");
        redisTemplate.boundHashOps("namehash").put("d","沙僧");
    }
    /**
     *提取所有的KEY
     */
    @Test
    public void testGetKeys(){
        //
        Set namehash = redisTemplate.boundHashOps("namehash").keys();
        System.out.println(namehash);
        List namehash1 = redisTemplate.boundHashOps("namehash").values();
        System.out.println(namehash1);
    }
    /**
     * 根据KEY提取值
     */
    @Test
    public void testGetValueByKey(){
        //String o = (String)redisTemplate.boundHashOps("itemCat").get("1");
        Long lg=(Long) redisTemplate.boundHashOps("itemCat").get("手机");
        Object brandList = redisTemplate.boundHashOps("brandList").get(35L);
        Object specList = redisTemplate.boundHashOps("specList").get((long)37);
        Object o = redisTemplate.boundHashOps("h1").get("itt");
        System.out.println(o);
        System.out.println(lg);
        System.out.println(brandList+"---->"+specList);
    }

    /**
     * 根据KEY移除值
     */
    @Test
    public void testRemoveValueByKey(){
        redisTemplate.boundHashOps("namehash").delete("c");
    }
}

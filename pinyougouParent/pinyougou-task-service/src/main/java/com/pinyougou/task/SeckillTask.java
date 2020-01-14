package com.pinyougou.task;

import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillGoodsExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * current system date 2020/1/14/014
 *
 * @author asura
 */
@Component
public class SeckillTask {

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    private TbSeckillGoodsMapper seckillGoodsMapper;


    @Scheduled(cron = "* * * * * ?")
    public void refreshSeckillGoods(){
        System.out.println("执行任务调度"+new Date());
        //查询所有的秒杀商品键集合
        List ids=new ArrayList(redisTemplate.boundHashOps("seckillGoods").keys());
        //查询正在秒杀的商品列表
        TbSeckillGoodsExample example = new TbSeckillGoodsExample();
        TbSeckillGoodsExample.Criteria criteria = example.createCriteria();
        //审核通过
        criteria.andStatusEqualTo("1");
        //剩余库存大于0
        criteria.andStockCountGreaterThan(0);
        //开始时间小于等于当前时间
        criteria.andStartTimeLessThanOrEqualTo(new Date());
        //结束时间大于当前时间
        criteria.andEndTimeGreaterThan(new Date());
        //排除缓存中已经有的商品
        criteria.andIdNotIn(ids);

        List<TbSeckillGoods> seckillGoods = seckillGoodsMapper.selectByExample(example);
        //装入缓存
        for (TbSeckillGoods seckillGood : seckillGoods) {
            redisTemplate.boundHashOps("seckillGoods").put(seckillGood.getId(),seckillGood);
        }
        System.out.println("将"+seckillGoods.size()+"条商品装入缓存");
    }

    /**
     * 移除秒杀商品
     */
    @Scheduled(cron="* * * * * ?")
    public void removeSeckillGoods(){
        System.out.println("移除秒杀商品任务在执行");
        //扫描缓存中秒杀商品列表，发现过期的移除
        List<TbSeckillGoods> seckillGoodsList =null;
//                redisTemplate.boundHashOps("seckillGoods").values();
        for (TbSeckillGoods goods : seckillGoodsList) {
            //如果结束日期小于当前日期，则表示过期
            if(goods.getEndTime().getTime()<System.currentTimeMillis()){
                //项数据库保存记录
                seckillGoodsMapper.updateByPrimaryKey(goods);
                //移除缓存数据
                redisTemplate.boundHashOps("seckillGoods").delete(goods.getId());
                System.out.println("移除秒杀商品"+goods.getId());
            }
        }
        System.out.println("移除秒杀商品任务结束");
    }

}

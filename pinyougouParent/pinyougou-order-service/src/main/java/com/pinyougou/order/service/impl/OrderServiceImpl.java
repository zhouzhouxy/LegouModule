package com.pinyougou.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.TbOrderItemMapper;
import com.pinyougou.mapper.TbOrderMapper;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojogroup.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import util.IdWorker;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * current system date 2020/1/12/012
 *
 * @author asura
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    private TbOrderMapper tbOrderMapper;

    @Autowired
    private TbOrderItemMapper orderItemMapper;

    @Autowired
    private IdWorker idWorker;


    /**
     * 增加
     */
    @Override
    public void add(TbOrder order) {
        //得到购物车数据
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(order.getUserId());
        for (Cart cart : cartList) {
            long orderID = idWorker.nextId();
            System.out.println("sellerId:"+cart.getSellerId());
            //新创建订单对象
            TbOrder tbOrder = new TbOrder();
            //订单ID
            tbOrder.setOrderId(orderID);
            //用户名
            tbOrder.setUserId(order.getUserId());
            //支付类型
            tbOrder.setPaymentType(order.getPaymentType());
            //状态：未付款
            tbOrder.setStatus("1");
            //订单创建日期
            tbOrder.setCreateTime(new Date());
            //订单更新日期
            tbOrder.setUpdateTime(new Date());
            //地址
            tbOrder.setReceiverAreaName(order.getReceiverAreaName());
            //手机号
            tbOrder.setReceiverMobile(order.getReceiverMobile());
            //收货人
            tbOrder.setReceiver(order.getReceiver());
            //订单来源
            tbOrder.setSourceType(order.getSourceType());
            //商家ID
            tbOrder.setSellerId(cart.getSellerId());
            //循环购物车明细
            double money=0;
            for (TbOrderItem orderItem : cart.getOrderItemList()) {
                orderItem.setId(idWorker.nextId());
                //订单ID
                orderItem.setOrderId(orderID);
                orderItem.setSellerId(cart.getSellerId());
                //金额累加
                money+= orderItem.getTotalFee().doubleValue();
                orderItemMapper.insert(orderItem);
            }
            tbOrder.setPayment(new BigDecimal(money));
            tbOrderMapper.insert(tbOrder);
        }
            redisTemplate.boundHashOps("cartList").delete(order.getUserId());
    }


}

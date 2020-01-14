package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pojo.Result;
import com.pinyougou.pojo.TbOrder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * current system date 2020/1/12/012
 * @author asura
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    @Reference(timeout = 6000)
    private OrderService orderService;

    @RequestMapping("/add")
    public Result add(@RequestBody TbOrder order){
        //获取当前登录人账号
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        order.setUserId(username);
        //订单来源 PC
        order.setSourceType("2");
        try {
            orderService.add(order);
            return  new Result(true,"增加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"增加失败");
        }
    }
}

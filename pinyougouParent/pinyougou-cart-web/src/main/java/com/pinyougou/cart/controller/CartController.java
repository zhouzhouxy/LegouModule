package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.pojo.Result;
import com.pinyougou.pojogroup.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import util.CookieUtil;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * current system date 2020/1/7/007
 *
 * @author asura
 */
//@CrossOrigin
@RestController
@RequestMapping("/cart")
public class CartController {

    //为了避免调用远程服务超时，我们可以将过期时间改为6秒（默认为1秒）
    @Reference(timeout = 6000)
    private CartService cartService;

    @Autowired
    private  HttpServletRequest request;

    @Autowired
    private  HttpServletResponse response;


    /**
     * 购物车列表
     * @return
     */
    @RequestMapping("/findCartList")
    public List<Cart> findCartList(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        String cartListString = CookieUtil.getCookieValue(request, "cartList", "UTF-8");
        if(cartListString==null||cartListString.equals("")){
            cartListString="[]";
        }
        List<Cart> cartList_cookie = JSON.parseArray(cartListString, Cart.class);
        if(username.equals("anonymousUser")) {
            //如果没有登录
            //读取本地购物车
            return cartList_cookie;
        }else{
            //如果已经登录就从redis中提取
            List<Cart> cartList_reids = cartService.findCartListFromRedis(username);
            if(cartList_cookie.size()>0){
                //合并购物车
                cartList_reids=cartService.mergeCartList(cartList_reids,cartList_cookie);
                //清除本地cookie的数据
                CookieUtil.deleteCookie(request,response,"cartList");
                //将合并后的数据存入redis
                cartService.saveCartListToRedis(username,cartList_reids);
            }
            return cartList_reids;
        }
    }

    /**
     * 添加商品到购物车
     * @param itemId
     * @param num
     * @return
     */
    @RequestMapping("/addGoodsToCartList")
    @CrossOrigin(origins="http://localhost:9106")
    public Result addGoodsToCartList(Long itemId,Integer num){
        //接收跨域请求
        //可以访问的域(档次方法不需要操作cookie)
//        response.setHeader("Access-Control-Allow-Origin", "http://localhost:9106");
        //如果操作cookie，必须要加上这句话
//        response.setHeader("Access-Control-Allow-Credentials", "true");
        //得到登录人账号，判断当前是否有人登陆
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("当前登录用户"+username);
        try {
            //获取购物车列表
            List<Cart> cartList = findCartList();
            cartList = cartService.addGoodsToCartList(cartList, itemId, num);
            if(username.equals("anonymousUser")){
                //如果是未登录，保存到cookie
                CookieUtil.setCookie(request,response,"cartList",JSON.toJSONString(cartList),3600*24,"utf-8");
                System.out.println("向cookie存入数据");
            }else{
                //如果是已经登录，保存到redis
                cartService.saveCartListToRedis(username,cartList);
            }
            return new Result(true,"添加成功");
        }catch(RuntimeException e) {
            e.printStackTrace();
            return new Result(false,e.getMessage());
        }catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"添加失败");
        }
    }

}

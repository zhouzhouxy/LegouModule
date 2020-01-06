package com.pinyougou.user.controller;

import com.pinyougou.pojo.Result;
import com.pinyougou.pojo.TbUser;
import com.pinyougou.user.service.UserService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import util.PhoneFormatCheckUtils;

@RestController
@RequestMapping("/user")
public class UserController {

   @Reference
   private UserService userService;

    @RequestMapping("/sendCode")
    public Result sendCode(String phone){
        //判断手机号格式
        if(!PhoneFormatCheckUtils.isPhoneLegal(phone)){
            return new Result(false,"手机号格式不正确");
        }
        try {
            userService.createSmsCode(phone);
            System.out.println(userService);
            return new Result(true,"验证码发送成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"验证码发送失败");

        }
    }

    @RequestMapping("/add")
    public Result add(@RequestBody TbUser user,String smscode){
        boolean b = userService.checkSmsCode(user.getPhone(), smscode);
        if(b==false){
            return new Result(false,"验证码输入错误！");
        }
        try {
            System.out.println(userService);
            userService.add(user);
            return new Result(true,"增加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"增加失败");
        }
    }
}

package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.User;
import com.itheima.reggie.service.UserService;
import com.itheima.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;
    @PostMapping("/sendMsg")
    public R<String> sendMsg(HttpServletRequest servletRequest, @RequestBody User user, HttpServletResponse servletResponse){
        //获取手机号
        String phone = user.getPhone();

        if (StringUtils.isNotEmpty(phone)){
            //生成四位验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            //发送短信
            log.info("生成的验证码为{}",code);
            //将验证码存储到session，真实项目中应该存在redis中并设置超时时间
            servletRequest.getSession().setAttribute(phone,code);
            R<String> stringR = new R<>();
            stringR.setCode(1);
            stringR.setData("发送短信成功！");
            stringR.setMsg(code);
//            return R.success("发送短信成功！");
            return stringR;
        }
        return R.error("发送短信失败！");
    }
    @PostMapping("/login")
    public R<User> lonin(HttpServletRequest servletRequest, @RequestBody Map map, HttpServletResponse servletResponse){
        //获取手机号
        String phone = (String) map.get("phone");
        //获取输入的验证码
        String code = (String) map.get("code");
        HttpSession session = servletRequest.getSession();
        Object codeInSession = session.getAttribute(phone);
        if (codeInSession!=null && codeInSession.equals(codeInSession)){
            LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
            userLambdaQueryWrapper.eq(User::getPhone,phone);
            User user = userService.getOne(userLambdaQueryWrapper);
            if (user == null){
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user",user.getId());
            return R.success(user);
        }

        return R.error("登录失败！");
    }
}

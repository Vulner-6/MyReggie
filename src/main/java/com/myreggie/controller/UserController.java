package com.myreggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.myreggie.common.R;
import com.myreggie.common.ValidateCodeUtils;
import com.myreggie.entity.User;
import com.myreggie.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

@Slf4j
@RestController   // 等同于 @Controller + @ResponseBody
@RequestMapping("/user")
public class UserController
{
    @Autowired
    private UserService userService;

    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession httpSession)
    {
        // 获取手机号
        String phone=user.getPhone();
        // 判断手机号是否为空
        if(StringUtils.isNotEmpty(phone))
        {
            // 生成4位验证码，发送短信
            String code=ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("已发送验证码！code:{}",code);  // 这里不使用真实的发送短信的方法了，要申请接口很麻烦
            httpSession.setAttribute(phone,code);  // 记录到 session 中
            return  R.success("手机验证码发送成功！");
        }

        return R.error("手机验证码发送失败！");
    }

    /**
     * 移动端用户登录
     * @param map
     * @param httpSession
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession httpSession)
    {
        log.info(map.toString());
        // 获取手机号，验证码
        String phone=map.get("phone").toString();
        String code=map.get("code").toString();
        // 从 session 中获取保存的验证码
        Object codeInSession=httpSession.getAttribute(phone);

        // 进行验证码对比（页面提交的验证码和Session中保存的验证码）
        if(codeInSession!=null && codeInSession.equals(code))
        {
            // 判断当前手机号是否是新用户，若新用户则注册
            LambdaQueryWrapper<User> lambdaQueryWrapper=new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(User::getPhone,phone);
            User user=userService.getOne(lambdaQueryWrapper);
            // 若是新用户则注册
            if(user==null)
            {
                user=new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            httpSession.setAttribute("user",user.getId());
            // 返回 user 对象
            return R.success(user);
        }


        return R.error("登录失败！");
    }

}

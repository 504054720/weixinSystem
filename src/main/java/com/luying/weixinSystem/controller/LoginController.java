package com.luying.weixinSystem.controller;

import com.luying.weixinSystem.service.SysUserInfoService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LoginController {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);
    @Autowired
    private SysUserInfoService sysUserInfoService;

    @RequestMapping("/doLogin")
    public String doLogin(@RequestParam String username, @RequestParam String password, RedirectAttributes redirectAttributes){
        Subject currentSubject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(username,password);
        try {
            currentSubject.login(token);
            Session session = SecurityUtils.getSubject().getSession();
            Map<String,String> user = sysUserInfoService.queryInfo(username);
            session.setAttribute("USER",user);
            return "redirect:/index";
        } catch (UnknownAccountException e) {
            LOGGER.info("loginContorller_exception:账号不存在！");
            redirectAttributes.addFlashAttribute("msg","账号或密码错误！");
            return "redirect:/login";
        } catch (IncorrectCredentialsException e) {
            redirectAttributes.addFlashAttribute("msg","账号或密码错误！");
            LOGGER.info("loginContorller_exception:密码错误！");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("msg","登录异常！");
            LOGGER.info("loginContorller_exception:登录异常！" + e);
            return "redirect:/login";
        }
    }
}

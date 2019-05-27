package com.luying.weixinSystem.controller;

import com.alibaba.fastjson.JSON;
import com.luying.weixinSystem.service.EmployeeBaseService;
import com.luying.weixinSystem.service.FuwuhaoService;
import com.luying.weixinSystem.service.WxOpenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/fuwuhao")
public class FuwuhaoController {
    private static final Logger LOGGER = LoggerFactory.getLogger(FuwuhaoController.class);
    @Autowired
    private FuwuhaoService fuwuhaoService;
    @Autowired
    private WxOpenService wxOpenService;
    @Autowired
    private EmployeeBaseService employeeBaseService;

    @GetMapping("/getUserInfo/{code}")
    public String getOpenId(@PathVariable String code){
        Map<String,String> paraMap = new HashMap<>();
        Map<String,Object> result = new HashMap<>();
        try {
            String openid = fuwuhaoService.getOppenid(code);
            //String openid = "okmFV1il0kT6OT9DRjV0-7K55uh0";
            paraMap.put("openid",openid);
            result.put("employee",employeeBaseService.queryEmployeBaseInfo(paraMap));
            return JSON.toJSONString(result);
        } catch (Exception e) {
            LOGGER.info("getOpenid_exception:" + e);
            return "1";
        }
    }
}

package com.luying.weixinSystem.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.luying.weixinSystem.service.WxOpenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/wxOpen")
public class WxOpenController {
    private static final Logger LOGGER = LoggerFactory.getLogger(WxOpenController.class);

    @Autowired
    private WxOpenService wxOpenService;

    @PostMapping("/add")
    public String insert(@RequestBody Map map){
        String result = "0";
        try {
            wxOpenService.insert(map);
        } catch (Exception e) {
            LOGGER.info("wxOpenInsert_exception:" + e);
            result = "1";
        }
        return result;
    }
    @PostMapping("/getWxOpenInfo")
    public String queryForList(@RequestBody Map map){
        String result = "";
        try {
            return JSON.toJSONString(wxOpenService.queryForList(map));
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.info("queryForList_exception:" + e);
            result = "-1";
        }
        return result;
    }
    @PostMapping("/binding")
    public String binding(@RequestBody Map map){
        String result = "0";
        try {
            wxOpenService.binding(map);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.info("binding_exception:" + e);
            result = "-1";
        }
        return result;
    }
    @PostMapping("/unBinding")
    public String unBinding(@RequestBody Map map){
        String result = "0";
        try {
            wxOpenService.unBinding(map);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.info("binding_exception:" + e);
            result = "-1";
        }
        return result;
    }

}

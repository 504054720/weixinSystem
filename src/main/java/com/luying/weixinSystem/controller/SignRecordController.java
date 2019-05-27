package com.luying.weixinSystem.controller;

import com.alibaba.fastjson.JSON;
import com.luying.weixinSystem.service.SignRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/signRecord")
public class SignRecordController {
    private static final Logger LOGGER = LoggerFactory.getLogger(SignRecordController.class);

    @Autowired
    private SignRecordService signRecordService;

    @PostMapping("/signOn")
    public String signOn(@RequestBody Map map){
        String result = "0";
        try {
            signRecordService.add(map);
        } catch (Exception e) {
            LOGGER.info("signRecord_signOn_exception:" + e);
            result = "1";
        }
        return result;
    }

    @PostMapping("/queryOne")
    public String queryOne(@RequestBody Map map){
        String result = "";
        try {
            result = JSON.toJSONString(signRecordService.queryOne(map));
        } catch (Exception e) {
            LOGGER.info("signRecord_queryOne_exception:" + e);
            result = "1";
        }
        return result;
    }

    @PostMapping("/signOff")
    public String signOff(@RequestBody Map map){
        String result = "0";
        try {
            signRecordService.update(map);
        } catch (Exception e) {
            LOGGER.info("signRecord_signOff_exception:" + e);
            result = "1";
        }
        return result;
    }
    @PostMapping("/getRecordByMonth")
    public String queryRecordByMonth(@RequestBody Map map){
        String result = "0";
        try {
            result = JSON.toJSONString(signRecordService.queryRecordByMonth(map));
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.info("queryRecordByMonth_exception:" + e);
            result = "1";
        }
        return result;
    }
    @PostMapping("/getSignWorkTime")
    public String querySignWorkTime(@RequestBody Map map){
        String result = "0";
        try {
            result = JSON.toJSONString(signRecordService.querySignWorkTime(map));
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.info("querySignWorkTime_exception:" + e);
            result = "1";
        }
        return result;
    }
}

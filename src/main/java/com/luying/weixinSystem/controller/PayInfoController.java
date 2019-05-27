package com.luying.weixinSystem.controller;

import com.alibaba.fastjson.JSON;
import com.luying.weixinSystem.service.FuwuhaoService;
import com.luying.weixinSystem.service.PayInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/payInfo")
public class PayInfoController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PayInfoController.class);
    @Autowired
    private PayInfoService payInfoService;
    @Autowired
    private  FuwuhaoService fuwuhaoService;

    @PostMapping("/queryYearMonth")
    public String queryYearMonth(@RequestBody Map map){
        String result = "0";
        try {
            result = JSON.toJSONString(payInfoService.queryYearMonth(map));
        } catch (Exception e) {
            LOGGER.info("queryYearMonth_exception:" + e);
            result = "1";
        }
        return result;
    }

    @PostMapping("/querySalayList")
    public String querySalayList(@RequestBody Map map){
        String result = "0";
        try {
            result = JSON.toJSONString(payInfoService.querySalaryInfoList(map));
        } catch (Exception e) {
            LOGGER.info("querySalayList_exception:" + e);
            result = "1";
        }
        return result;
    }

    @PostMapping("/sendSalaryInfo")
    public String sendSalaryInfo(@RequestParam MultipartFile file,@RequestParam String salaryMonth){
        String result = "0";
        try {
            payInfoService.importSalaryInfo(file,salaryMonth);
            fuwuhaoService.sendMessageToAll(salaryMonth);
        } catch (Exception e) {
            LOGGER.info("sendSalaryInfo_exception:" + e);
            result = "1";
        }
        return result;

    }

}

package com.luying.weixinSystem.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.luying.weixinSystem.service.EmployeeBaseService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/employee")
public class EmployeeController {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmployeeController.class);
    @Autowired
    private EmployeeBaseService employeeBaseService;

    @GetMapping("/queryAll")
    public String queryAll(){
        String result = "";
        try {
            result = JSONObject.toJSONString(employeeBaseService.queryAll());
        } catch (Exception e) {
            LOGGER.info("EmployeeController_queryAll_exception:"+e);
            result = "-1";
        }
        return result;
    }
    @GetMapping("/query")
    public String query(@RequestParam String mobile){
        Map map = new HashMap();
        map.put("mobile",mobile);
        String result = "";
        try {
            result = JSONObject.toJSONString(employeeBaseService.query(map));
        } catch (Exception e) {
            LOGGER.info("EmployeeController_queryAll_exception:"+e);
            result = "-1";
        }
        return result;
    }

    @PostMapping("/import")
    public String importEmployeeInfo(@RequestPart("file") MultipartFile file){
        return employeeBaseService.insert(file);
    }

    @PostMapping("/modifyMobile")
    public String modifyMobile(@RequestBody Map map){
        String result = "0";
        try {
            employeeBaseService.midifyMobile(map);
        } catch (Exception e) {
            LOGGER.info("modifyMobile_exception:" + e);
            result = "1";
        }
        return result;
    }

    @PostMapping("/queryEmployeBaseInfo")
    public String queryEmployeBaseInfo(@RequestBody Map map){
        String result = "0";
        try {
            result = JSON.toJSONString(employeeBaseService.queryEmployeBaseInfo(map));
        } catch (Exception e) {
            LOGGER.info("queryEmployeBaseInfo_exception:" + e);
            result = "1";
        }
        return result;
    }

    @PostMapping("/addOne")
    public String addOne(@RequestBody Map map){
        String result = "0";
        try {
            result = employeeBaseService.addOne(map);
        } catch (Exception e) {
            LOGGER.info("addOne_exception:" + e);
            result = "-1";
        }
        return result;
    }
    @GetMapping("/queryOne/{id}")
    public String queryOne(@PathVariable String id){
        Map<String,String> paraMap = new HashMap<>();
        paraMap.put("id",id);
        try {
            return  JSON.toJSONString(employeeBaseService.queryOne(paraMap));
        } catch (Exception e) {
            LOGGER.info("queryOne_exception" + e);
            return "1";
        }
    }

    @PostMapping("/editOne")
    public String editOne(@RequestBody Map map){
        String result = "0";
        try {
            employeeBaseService.updateOne(map);
        } catch (Exception e) {
            LOGGER.info("modifyMobile_exception:" + e);
            result = "-1";
        }
        return result;
    }

    @PostMapping("/del")
    public String del(@RequestBody Map map){
        String result = "0";
        try {
            employeeBaseService.del(map);
        } catch (Exception e) {
            LOGGER.info("del_exception:" + e);
            result = "-1";
        }
        return result;
    }
    @PostMapping("/getEmployeeInfo")
    public String queryForList(@RequestBody Map map){
        String result = "";
        try {
            result = JSON.toJSONString(employeeBaseService.queryForList(map));
        } catch (Exception e){
            e.printStackTrace();
            LOGGER.info("queryForList_exception:" + e);
            result = "-1";
        }
        return result;
    }

}

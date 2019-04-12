package com.luying.weixinSystem.controller;

import com.luying.weixinSystem.service.WeixinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class WeixinController {
    @Autowired
    WeixinService weixinService;


    @PostMapping("/sendMessage")
    @ResponseBody
    public String sendMessage(@RequestPart("file") MultipartFile file,@RequestParam(required = false) String[] checkedInfos){

        if(file.isEmpty()){
            return "上传附件不能为空";
        }
        String fileName =file.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        if(!".xlsx".equals(suffix) && !".xls".equals(suffix)){
            return  "请上传.xlsx或.xls类型文件";

        }
        return weixinService.sendMessage(file,checkedInfos);
    }
    @GetMapping("/index")
    public String index(){
        return "index";
    }

    @GetMapping("/getAddressList/{departmentId}")
    @ResponseBody
    public String getAddressList(@PathVariable String departmentId){
        return weixinService.getAddressList();
    }
}

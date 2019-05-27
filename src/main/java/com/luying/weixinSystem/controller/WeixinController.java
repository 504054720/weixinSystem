package com.luying.weixinSystem.controller;

import com.alibaba.fastjson.JSONObject;
import com.luying.weixinSystem.service.WeixinService;
import com.luying.weixinSystem.util.WeixinClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Controller
public class WeixinController {
    @Autowired
    WeixinService weixinService;

    @ResponseBody
    @PostMapping("/recoredSign")
    public int recoredSign(@RequestParam("deptid") String deptid,
                           @RequestParam("ruleid") String ruleid,
                           @RequestParam("dist") String dist,
                           @RequestParam("userid") String userid,
                           @RequestParam("phone") String phone,
                           @RequestParam("xponit") String xponit,
                           @RequestParam("yponit") String yponit,
                           @RequestParam("scope") String scope,
                           @RequestParam("amt") String amt,
                           @RequestParam("pmt") String pmt
    ){

        return  weixinService.recoredSign(deptid ,
                ruleid,
                dist,
                userid ,
                phone,
                xponit ,
                yponit,scope,amt,pmt );

    }

    @GetMapping("/location")
    @ResponseBody
    public String location(@RequestParam("noncestr") String noncestr,
                           @RequestParam("timestamp") String timestamp,
                           @RequestParam("url") String url){

//1、获取AccessToken   消息助手
        String accessToken = weixinService.getSignAppAccessToken(); // 打卡应用
                                        //.getAccessToken();  // 消息助手

        //2、获取Ticket

//        String c = weixinService.getJsapiTicket(accessToken); // 企业自定义应用
        String c = weixinService.getQYJsapiTicket(accessToken);//   企业ok
        JSONObject obj=   (JSONObject)JSONObject.parse(c);
        String jsapi_ticket= obj.get("ticket").toString();

        //3、时间戳和随机字符串
//        String noncestr = UUID.randomUUID().toString().replace("-", "").substring(0, 16);//随机字符串
//        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);//时间戳

        System.out.println("accessToken:"+accessToken+"\njsapi_ticket:"+jsapi_ticket+"\n时间戳："+timestamp+"\n随机字符串："+noncestr);



        //5、将参数排序并拼接字符串
        String str = "jsapi_ticket="+jsapi_ticket+"&noncestr="+noncestr+"&timestamp="+timestamp+"&url="+url;

        //6、将字符串进行sha1加密
        String signature =weixinService.SHA1(str);
        System.out.println("参数："+str+"\n签名："+signature);
        Map<String,String> p=new HashMap<String,String>();
        p.put("nature" ,signature);
        String a= JSONObject.toJSONString(p);
        return a;
    }
    @GetMapping("/getAccessToken")
    @ResponseBody
    public String getAccessToken(){
        return  weixinService.getAccessToken();
    }

    @GetMapping("/getUserIdByCode")
    @ResponseBody
    public String getUserIdByCode(@RequestParam("token") String token,@RequestParam("code") String code){
        return  weixinService.getUserIdByCode(token,code);
    }

    @PostMapping("/sendMessage")
    @ResponseBody
    public String sendMessage(@RequestPart("file") MultipartFile file,@RequestParam(required = false) String[] checkedInfos,@RequestParam String salaryMonth){

        if(file.isEmpty()){
            return "上传附件不能为空";
        }
        String fileName =file.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        if(!".xlsx".equals(suffix) && !".xls".equals(suffix)){
            return  "请上传.xlsx或.xls类型文件";

        }
        return weixinService.sendMessage(file,checkedInfos,salaryMonth);
    }

    @GetMapping("/getAddressList/{departmentId}")
    @ResponseBody
    public String getAddressList(@PathVariable String departmentId){
        return weixinService.getAddressList();
    }
    @GetMapping("/getUserInfoByCode/{code}")
    @ResponseBody
    public String getUserInfoByCode(@PathVariable String code) throws Exception{
        String a=weixinService.getUserInfoByCode(code);
        System.out.print(a);
        return a;
    }

}

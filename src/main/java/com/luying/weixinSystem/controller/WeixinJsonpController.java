package com.luying.weixinSystem.controller;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.alibaba.fastjson.JSONObject;
import com.luying.weixinSystem.service.SignRuleService;
import com.luying.weixinSystem.service.WeixinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
public class WeixinJsonpController {
    @Autowired
    WeixinService weixinService;

    @Autowired
    SignRuleService signRuleService;

    @GetMapping("/updateUserRulejsonp")
    @ResponseBody
    public String updateUserRulejsonp(@RequestParam(name = "userid") String userid,
                                 @RequestParam(name = "ruleid") String ruleid,
                                 @RequestParam("callback") String callback){
        Map<String ,String> m=new HashMap<>();
       String result="0";
        try {
            m.put("userid",userid);
            m.put("ruleid",ruleid);
            signRuleService.updatUserRule(m);
        } catch (Exception e) {
            e.printStackTrace();
           // LOGGER.info( "signRule_updateUserRulejsonp_exception: " +e );
            result="-1";
        }
        return   callback+"("+result+")";
    }

    @GetMapping("/insertjsonp")
    @ResponseBody
    public String insertjsonp(@RequestParam(name = "ruletype") String ruletype,
                         @RequestParam(name = "scope") String scope,
                         @RequestParam(name = "amtime") String amtime,
                         @RequestParam(name = "pmtime") String pmtime,
                         @RequestParam(name = "companyname") String companyname,
                         @RequestParam(name = "xypoint") String xypoint,
                         @RequestParam("callback") String callback){
        String result = "0";
        try {
            Map<String, Object> map=new HashMap<String, Object>();
            map.put("type",ruletype);
            map.put("name",companyname);
            map.put("department",'1');
            map.put("longitude",xypoint.split(",")[0]);
            map.put("latitude",xypoint.split(",")[1]);
            map.put("scope",scope);
            map.put("workTime",amtime);
            map.put("offWorkTime",pmtime);
            map.put("creat_time",new Date());
            map.put("modify_time",new Date());
            signRuleService.insert(map);
        } catch (Exception se) {
          //  LOGGER.info("signRule_insert_exception:" + se);
            result = "-1";
        }
        return   callback+"("+result+")";
    }
    @ResponseBody
    @GetMapping("/recoredSignjsonp")
    public String recoredSignjsonp(@RequestParam("deptid") String deptid,
                           @RequestParam("ruleid") String ruleid,
                           @RequestParam("dist") String dist,
                           @RequestParam("userid") String userid,
                           @RequestParam("phone") String phone,
                           @RequestParam("xponit") String xponit,
                           @RequestParam("yponit") String yponit,
                           @RequestParam("scope") String scope,
                           @RequestParam("amt") String amt,
                           @RequestParam("pmt") String pmt,@RequestParam("callback") String callback
    ){

        int a=  weixinService.recoredSign(deptid , ruleid,  dist, userid ,  phone, xponit ,
                yponit,scope,amt,pmt );
        return callback+"("+a+")";

    }

    @GetMapping("/locationjsonp")
    @ResponseBody
    public String locationjsonp(@RequestParam("noncestr") String noncestr,
                           @RequestParam("timestamp") String timestamp,
                           @RequestParam("url") String url,
                           @RequestParam("callback") String callback){
        String accessToken = weixinService.getSignAppAccessToken(); // 打卡应用
        String c = weixinService.getQYJsapiTicket(accessToken);//   企业ok
        JSONObject obj=   (JSONObject)JSONObject.parse(c);
        String jsapi_ticket= obj.get("ticket").toString();
        String str = "jsapi_ticket="+jsapi_ticket+"&noncestr="+noncestr+"&timestamp="+timestamp+"&url="+url;
        String signature =weixinService.SHA1(str);
//        System.out.println("jsonp参数："+str+"\n签名："+signature);
        Map<String,String> p=new HashMap<String,String>();
        p.put("signature" ,signature);
       String a= JSONObject.toJSONString(p);
        return  callback+"("+a+")";

    }
    @GetMapping("/queryUserRulejsonp/{userid}")
    @ResponseBody
    public String queryUserRulejsonp(@PathVariable  String  userid,@RequestParam("callback") String callback){

        Map<String,String> p=new HashMap<String,String>();
        p.put("userid" ,userid);
        String ruleid=   signRuleService.queryUserRule(p);// ruleid
        p.put("ruleid",ruleid);
        Map<String,Object>  m=null;
        if(ruleid=="" || ruleid==null){
            //添加公司默认
            m=   signRuleService.queryFirstOneRule(p) ;
            p.put("ruleid", m.get("id").toString());
            try {
                signRuleService.insertUserRule(p);
            } catch (Exception e) {

                return "";
            }
        }
        if(m==null || m.size()==0){
            m= signRuleService.query(p).get(0);
        }
        String a= JSONObject.toJSONString(m);
        System.out.print("======="+a);
        return   callback+"("+a+")";
    }
    @GetMapping("/queryjsonp")
    @ResponseBody
    public String queryjsonp(@RequestParam("callback") String callback ){
        Map<String,String> paraMap = new HashMap<>();
        String a= JSONObject.toJSONString(signRuleService.query(paraMap));
        System.out.print("=========="+a);
        return   callback+"("+a+")";
    }

    @GetMapping("/getUserInfoByCodejsonp/{code}")
    @ResponseBody
    public String getUserInfoByCodejsonp(@PathVariable String code,@RequestParam("callback") String callback) throws Exception{
        String a=weixinService.getUserInfoByCode(code);
        System.out.print(a);
        return   callback+"("+a+")";
    }
    @GetMapping("/getAddressListJson")
    public void getAddressListJson(HttpServletRequest request, HttpServletResponse response) throws Exception{
        System.out.println("getAddressListJson start...");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        String callback = request.getParameter("callback");
        String result = callback + "(" + weixinService.getAddressList() + ")";
        response.getWriter().write(result);
    }
    @PostMapping("/sendMessageJson")
    public void sendMessage(HttpServletRequest request,HttpServletResponse response,@RequestPart("file") MultipartFile file, @RequestParam(required = false) String[] checkedInfos) throws  Exception{

        String callback = request.getParameter("callback");
        //response.getWriter().write(callback + "(" + weixinService.sendMessage(file,checkedInfos) + ")");
    }

    @GetMapping("/getUserInfoByCodeJsonp")
    public void getUserInfoByCode(HttpServletRequest request,HttpServletResponse response) throws Exception{
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        String code = request.getParameter("code");
        System.out.println("code=====:"+code);
        String callback = request.getParameter("callback");
      //  return weixinService.getUserInfoByCode(code);
        response.getWriter().write(callback + "(" + weixinService.getUserInfoByCode(code) + ")");
    }
}

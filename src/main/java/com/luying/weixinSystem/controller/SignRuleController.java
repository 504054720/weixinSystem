package com.luying.weixinSystem.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.luying.weixinSystem.service.SignRuleService;
import com.luying.weixinSystem.service.TPSignRecordService;
import com.luying.weixinSystem.util.TimeUtils;
import com.luying.weixinSystem.util.http.HttpResult;
import com.luying.weixinSystem.util.http.HttpService;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.*;

@RestController
@RequestMapping("/signRule")
public class SignRuleController {
    private static Logger LOGGER = LoggerFactory.getLogger(SignRuleController.class);
    @Autowired
    private SignRuleService signRuleService;

    @Autowired
    TPSignRecordService tPSignRecordService;

    @Autowired
    HttpService httpService;

    @GetMapping("/initdays")
    public String initdays(){
       //long l1= System.currentTimeMillis();
        String url="";
        Calendar c = Calendar.getInstance();
        int year=c.get(Calendar.YEAR);
        List<Map> list=new ArrayList<>();
        Map<String,Object> map=null;
        HttpResult result=null;
        for (int i=1 ;i<13;i++){
            int daysofmonth= TimeUtils.daysofmonth(year,i);
            for (int j=1;j<=daysofmonth;j++){
                map=new HashMap<>();
                map.put("id",TimeUtils.formatDateString(year, i, j));
                map.put("year",year);
                map.put("month",i <= 9 ? "0" + i : i);
                map.put("status",0);
                url="http://api.goseek.cn/Tools/holiday?date="+ TimeUtils.formatyyyyMMdd(year, i, j);
               // System.out.println( "==== " + url);
                try {
                    result= httpService.doGetlong(url);
                    JSONObject jsonObject=JSONObject.parseObject(result.getBody());
                   // int code=  Integer.parseInt(jsonObject.get("code").toString()) ;
                   // int data=  Integer.parseInt(jsonObject.get("data").toString());
                  //  System.out.println( "====getJson返回结果code is " + code+"====data is "+data);
                    map.put("status",jsonObject.get("data"));
                    list.add(map);
                } catch (Exception e) {
                    LOGGER.info("signRule_initdays_httpService_doGetlong"+e);
                    return "-1";
                }
            }// inner for
        }//outer for
        signRuleService.initdays(list);
        //System.out.println("耗时==开始时间 is "+l1+"结束时间 is " +System.currentTimeMillis());
        return "0";
    }


    @PostMapping("/updatedays")
    public String updatedays(@RequestParam(name = "ymd") String ymd,
                                 @RequestParam(name = "status") String status){
        Map<String ,String> m=new HashMap<>();
        try {
            m.put("id",ymd);
            List list= signRuleService.querydays(m);
            m.put("status",status);
            if(list==null || list.size()==0){
                m.put("year",ymd.split("-")[0]);
                m.put("month",ymd.split("-")[1]);
                list=new ArrayList<>();
                list.add(m);
                signRuleService.initdays(list);
            }else{
                signRuleService.updatedays(m);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.info( "signRule_updatedays_exception: " +e );
            return "-1";
        }
        return "0";
    }

    @GetMapping("/querySign")
    public String querySign(@RequestParam(name="account") String account ,
                            @RequestParam(name="stime") String stime,
                            @RequestParam(name="phone") String phone){
        Map m=new HashMap<>();
        m.put("userid",account);
        m.put("signtime",stime);
        m.put("phone",phone);
        return JSONObject.toJSONString(tPSignRecordService.querySign(m));

    }


    @PostMapping("/updateUserRule")
    public String updateUserRule(@RequestParam(name = "userid") String userid,
                                 @RequestParam(name = "ruleid") String ruleid){
        Map<String ,String> m=new HashMap<>();
        try {
            m.put("userid",userid);
            m.put("ruleid",ruleid);
            signRuleService.updatUserRule(m);
        } catch (Exception e) {
                e.printStackTrace();
            LOGGER.info( "signRule_updateUserRule_exception: " +e );
            return "-1";
        }
        return "0";
    }

    @GetMapping("/query")
    public String query(){
        Map<String,String> paraMap = new HashMap<>();
        //System.out.println(JSONObject.toJSONString(signRuleService.query(paraMap)));
        return JSONObject.toJSONString(signRuleService.query(paraMap));
    }

    @GetMapping("/queryUserRule/{userid}")
    public String queryUserRule(@PathVariable  String  userid){

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
                LOGGER.info("signRule_insertUserRule_exception:" +e);
                return "";
            }
        }
        if(m==null || m.size()==0){
            m= signRuleService.query(p).get(0);
        }
        return   JSONObject.toJSONString(m);
    }
    @PostMapping("/insert")
    public String insert(@RequestParam(name = "ruletype") String ruletype,
                         @RequestParam(name = "scope") String scope,
                         @RequestParam(name = "amtime") String amtime,
                         @RequestParam(name = "pmtime") String pmtime,
                         @RequestParam(name = "companyname") String companyname,
                         @RequestParam(name = "xypoint") String xypoint){
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
            LOGGER.info("signRule_insert_exception:" + se);
            result = "-1";
        }
        return result;
    }
    @PostMapping("/add")
    public String add(@RequestBody Map map){
        String result = "0";
        try {
            signRuleService.insert(map);
        } catch (Exception e) {
            LOGGER.info("signRule_insert_exception:" + e);
            result = "1";
        }
        return result;
    }

    @PostMapping("/edit")
    public String edit(@RequestBody Map map){
        String result = "0";
        try {
            LOGGER.info("edit_para:"+map.toString());
            signRuleService.update(map);
        } catch (Exception e) {
            LOGGER.info("signRule_edit_exception:" + e);
            result = "1";
        }
        return result;
    }
    @PostMapping("/delete")
    public String delete(@RequestBody Map map){
        String result = "0";
        if (map == null || map.get("ruleId") == null || "".equals(map.get("ruleId"))) {
            return "1";
        }
        try {
            signRuleService.delete(map);
        } catch (Exception e) {
            LOGGER.info("signRule_delete_exception:" + e);
            result = "1";
        }
        return result;
    }

    @GetMapping("/getById/{ruleid}")
    public String queryById(@PathVariable String ruleid){
        Map<String,Object> map = new HashMap<>();
        map.put("ruleid",ruleid);
        return JSON.toJSONString(signRuleService.query(map));
    }

    @PostMapping("/employeeRuleModify")
    public String employeeRuleModify(@RequestBody Map map){
        String result = "0";
        try {
            signRuleService.employeeRuleModify(map);
        } catch (Exception e) {
            LOGGER.info("employeeRuleModify_exception:" + e);
            result = "1";
        }
        return result;
    }
}

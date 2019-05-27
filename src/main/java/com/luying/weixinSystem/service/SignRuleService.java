package com.luying.weixinSystem.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.luying.weixinSystem.mapper.EmployeeSignRuleMapper;
import com.luying.weixinSystem.mapper.SignRuleMapper;
import com.luying.weixinSystem.mapper.TPSignRecordMapper;
import com.luying.weixinSystem.util.IdGernerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class SignRuleService {
    @Autowired
    private SignRuleMapper signRuleMapper;
    @Autowired
    private EmployeeSignRuleMapper employeeSignRuleMapper;

    public void initdays(List list){
        signRuleMapper.initdays(list);
    }
    public void updatedays(Map p){
        signRuleMapper.updatedays(p);
    }
    public void updatedaystoholiday(List list){
        signRuleMapper.updatedaystoholiday(list);
    }
    public void updatedaystoweekend(List list){
        signRuleMapper.updatedaystoweekend(list);
    }
    public List<Map<String,Object>> querydays(Map p){
          return  signRuleMapper.querydays(p);
    }


    public  Map<String,Object> queryFirstOneRule(Map p){
       return   signRuleMapper.queryFirstOneRule(p) ;
    }

    public void insertUserRule(Map p) throws Exception{
       signRuleMapper.insertUserRule(p);
    };
    public String  queryUserRule(Map p){
        Map<String,Object> m=  signRuleMapper.queryUserRule(p);
        if(m==null || m.size()==0){
            return "";
        }
        return  m.get("ruleid").toString();
    };
    public void updatUserRule(Map p){
           signRuleMapper.updatUserRule(p);
    };

    public List<Map<String,Object>> query(Map paraMap){
        return  signRuleMapper.query(paraMap) ;
    }

    public void insert(Map map) throws Exception {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:ss:mm");
        map.put("creatTime",simpleDateFormat.format(date));
        map.put("modifyTime",simpleDateFormat.format(date));
        map.put("id", IdGernerator.next());
        signRuleMapper.insert(map);
    }
    public void update(Map map) throws Exception{
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:ss:mm");
        map.put("modifyTime",simpleDateFormat.format(date));
        signRuleMapper.update(map);
    }
    public void delete(Map map) throws Exception{
        signRuleMapper.delete(map);
    }

    public void employeeRuleModify(Map map) throws Exception{
        Map querMap = employeeSignRuleMapper.query(map);
        if (querMap == null) {
            employeeSignRuleMapper.insert(map);
        } else {
            employeeSignRuleMapper.update(map);
        }
    }
}

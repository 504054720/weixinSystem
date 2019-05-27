package com.luying.weixinSystem.service;

import com.luying.weixinSystem.mapper.SignRecordMapper;
import com.luying.weixinSystem.mapper.SignRuleMapper;
import com.luying.weixinSystem.util.IdGernerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class SignRecordService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SignRecordService.class);
    @Autowired
    private SignRecordMapper signRecordMapper;
    @Autowired
    private SignRuleMapper signRuleMapper;

    public void add(Map map) throws Exception{
        map.put("id", IdGernerator.next());
        map.put("signDate",getCurDate());
        signRecordMapper.insert(map);
    }

    public void update(Map map) throws Exception{
        map.put("signDate",getCurDate());
        signRecordMapper.update(map);
    }

    public Map<String,String> queryOne(Map map) throws Exception{
        map.put("signDate",getCurDate());

        //0:未签到  1：已签到过，提示是否重签(一小时内)  2：未到签退时间  3：过了签退时间，正常签退  4：已签退，提示是否重签
       Map<String,String> resultMap = signRecordMapper.queryOne(map);
       if (resultMap != null) {
           String workTime = resultMap.get("sign_work_time");
           String offWorkTime = resultMap.get("sign_off_work_time");
           if (workTime != null && offWorkTime == null ) {
               if (checkTime(workTime,3600000)) {
                   resultMap.put("signFlag","1");
               } else {
                   if(checkTime("18:00:00",0)) {
                       resultMap.put("signFlag","2");
                   } else {
                       resultMap.put("signFlag","3");
                   }
               }
           }
           if (workTime != null && offWorkTime != null) {
               resultMap.put("signFlag","4");
           }

       }

        return resultMap;
    }

    /**
     * 与当前时间比较
     * @param strTime
     * @return
     * @throws Exception
     */
    public static boolean checkTime(String strTime,long interval) throws Exception{
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        long currentTime = simpleDateFormat.parse(simpleDateFormat.format(date)).getTime();
        long longTime = simpleDateFormat.parse(strTime).getTime();
        long val = currentTime - longTime;
        if (val <= interval) {
            return true;
        }
        return  false;
    }

    /**
     * 获取当前日期
     * @return
     */
    public static String getCurDate(){
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(date);
    }

    /**
     * 计算打卡状态
     * @throws Exception
     */
    public void signRecordCountTask() throws Exception{
        Map<String,String> paraMap = new HashMap<>();
        paraMap.put("signDate",getYesterday());
        //正常工作日0, 法定节假日1, 节假日调休补班2，周末3
        String signDateState = signRecordMapper.queryDayStatus(paraMap);
        if ("0".equals(signDateState) || "2".equals(signDateState)) {
            //打卡规则Map
            Map<String,Map<String,Object>> ruleMap = this.getRuleMap();

            List<Map<String,String>> signRecordList = signRecordMapper.queryAllYesterday(paraMap);
            if (signRecordList != null && signRecordList.size() >= 1) {

                for (Map<String,String> map : signRecordList) {
                    Map updateMap = new HashMap();
                    String state = getSignState(map,ruleMap,signDateState);
                    updateMap.put("signRecordId",map.get("id"));
                    updateMap.put("state",state);
                    signRecordMapper.updateState(updateMap);
                }
            }
        }
    }

    public static String getSignState(Map<String,String> signMap,Map<String,Map<String,Object>> ruleMap,String signDateState) throws Exception{
        //0:正常；1：迟到；2：早退；3：迟到、早退；4：旷工；5：缺打卡
        String state = "0";
        String signWorkTime = signMap.get("sign_work_time");
        String signOffWorkTime = signMap.get("sign_off_work_time");
        if (signWorkTime == null || signOffWorkTime == null) {
            return "5";
        }
        String ruleid = String.valueOf(signMap.get("rule_id"));
        String ruleWorkTime = ruleMap.get(ruleid).get("work_time").toString();
        String ruleOffWorkTime = ruleMap.get(ruleid).get("off_work_time").toString();

        if (!compareTime(signWorkTime,ruleWorkTime)) {
            state = "1";
            if (!compareTime(ruleOffWorkTime,signOffWorkTime)) {
                state = "3";
            }
        } else {
            if (!compareTime(ruleOffWorkTime,signOffWorkTime)) {
                state = "2";
            }
        }

        return state;
    }

    public static boolean compareTime(String beforTime,String afterTime) throws Exception{
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        if ((simpleDateFormat.parse(afterTime).getTime() - simpleDateFormat.parse(beforTime).getTime()) >=0) {
            return true;
        }
        return  false;
    }

    public Map<String,Map<String,Object>> getRuleMap() {
        List<Map<String,Object>>  ruleList = signRuleMapper.query(new HashMap());
        if (ruleList != null && ruleList.size() >=1) {
            Map<String,Map<String,Object>> resultMap = new HashMap<>();
            for (Map<String,Object> map : ruleList) {
                resultMap.put(map.get("id").toString(),map);
            }
            return resultMap;
        }
        return null;
    }
    public static String getYesterday(){
        Calendar calendar = Calendar.getInstance();
        String month = (calendar.get(Calendar.MONTH) + 1) + "" ;
        String date = (calendar.get(Calendar.DAY_OF_MONTH) - 1) + "";
        String monthF = month.length() == 1 ? "0" + month : month;
        String dateF = date.length() == 1 ? "0" + date : date;
        StringBuilder yesterday = new StringBuilder(calendar.get(Calendar.YEAR)+"");
        yesterday.append("-" + monthF).append("-" + dateF);
        return yesterday.toString();
    }

    public List<Map<String,Object>> queryRecordByMonth(Map map) throws Exception{
        return signRecordMapper.queryRecordByMonth(map);
    }
    public Map<String,String> querySignWorkTime(Map map) throws Exception{
        return signRecordMapper.queryOne(map);
    }
}

package com.luying.weixinSystem.service;

import com.luying.weixinSystem.mapper.PayInfoMapper;
import com.luying.weixinSystem.mapper.WxOpenMapper;
import com.luying.weixinSystem.message.TextMessage;
import com.luying.weixinSystem.util.EncryptionUtil;
import com.luying.weixinSystem.util.FuwuhaoClient;
import com.luying.weixinSystem.util.MessageUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class FuwuhaoService {
    private static final Logger LOGGER = LoggerFactory.getLogger(FuwuhaoService.class);

    @Value("${fuwuhao.appid}")
    private String appid;
    @Value("${fuwuhao.secret}")
    private String secret;

    @Autowired
    private FuwuhaoClient fuwuhaoClient;
    @Autowired
    private WxOpenMapper wxOpenMapper;

    /**
     * 群发消息
     * @param salaryYearMonth
     * @return
     * @throws Exception
     */
    public String sendMessageToAll(String salaryYearMonth) throws Exception{
        String accessToken = fuwuhaoClient.getAccessToken(appid,secret);
        String content = salaryYearMonth + "工资条已发放，可到个人中心-工资查看！【测试】";

        Map<String,Object> paraMap = new HashMap<>();
        paraMap.put("accessToken",accessToken);
        Map<String,Object> filterMap = new HashMap<>();
        filterMap.put("is_to_all",true);
        Map<String,Object> textMap = new HashMap<>();
        textMap.put("content",content);
        paraMap.put("filter",filterMap);
        paraMap.put("text",textMap);
        paraMap.put("msgtype","text");

        String result = fuwuhaoClient.sendMessageToAll(paraMap);
        return result;
    }

    /**
     * code获取openid
     * @param code
     * @return
     * @throws Exception
     */
    public String getOppenid(String code) throws Exception{
        return fuwuhaoClient.getOpenid(appid,secret,code);
    }
    public String handleTextMessage(Map<String,String> reqMap) throws Exception{
        String resultMessage = "";

        //用户账号（openid）
        String fromUserName = reqMap.get("FromUserName");
        //公众号
        String toUserName = reqMap.get("ToUserName");
        //消息类型
        String msgType = reqMap.get("MsgType");
        //消息内容
        String content = reqMap.get("Content");
        LOGGER.info("fromUserName:" + fromUserName + " toUserName:"+toUserName+" msgType:"+msgType+" content:"+content);

        if("text".equals(msgType)){
            TextMessage textMessage = new TextMessage();
            textMessage.setToUserName(fromUserName);
            textMessage.setFromUserName(toUserName);
            textMessage.setCreateTime(new Date().getTime());
            textMessage.setMsgType(msgType);
//            if (StringUtil.isMobile(content)) {
//                Map map = new HashMap();
//                map.put("openid",fromUserName);
//                map.put("mobile",content);
//                if (wxOpenMapper.queryCount(map) <= 0) {
//                    wxOpenMapper.add(map);
//                    textMessage.setContent("手机号设置成功！");
//                } else {
//                    wxOpenMapper.update(map);
//                    textMessage.setContent("手机号更新成功！");
//                }
//
//            } else {
//                textMessage.setContent("请发送你本人的手机号，谢谢！");
//            }
            textMessage.setContent(content);
            resultMessage = MessageUtil.textMessageToXml(textMessage);
        }
        return resultMessage;
    }

    public void handleEventMessage(Map<String,String> reqMap){
        try {
            //用户账号（openid）
            String fromUserName = reqMap.get("FromUserName");
            //公众号
            String toUserName = reqMap.get("ToUserName");
            //消息类型
            String msgType = reqMap.get("MsgType");
            //事件类型
            String event = reqMap.get("Event");
            String createTime = reqMap.get("CreateTime");
            LOGGER.info("fromUserName:" + fromUserName + " toUserName:"+toUserName+" msgType:"+msgType+" event:"+event);

            if ("subscribe".equals(event)) {
                Map<String,Object> map = new HashMap<>();
                map.put("openid",fromUserName);
                map.put("createTime",createTime);
                map.put("bindingState","0");
                String accessToken = fuwuhaoClient.getAccessToken(appid,secret);
                Map<String,String> userInfoMap = fuwuhaoClient.getUserInfoByOpenid(accessToken,fromUserName);
                if (userInfoMap != null){
                    map.put("headimgurl",userInfoMap.get("headimgurl"));
                    map.put("nickname",userInfoMap.get("nickname"));
                }
                wxOpenMapper.add(map);
            }
            if ("unsubscribe".equals(event)) {
                wxOpenMapper.delete(fromUserName);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.info("event_exception:" + e);
        }
    }

}

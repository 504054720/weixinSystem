package com.luying.weixinSystem.service;

import com.luying.weixinSystem.mapper.WxOpenMapper;
import com.luying.weixinSystem.message.TextMessage;
import com.luying.weixinSystem.util.MessageUtil;
import com.luying.weixinSystem.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class MessageService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageService.class);

    @Autowired
    private WxOpenMapper wxOpenMapper;

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
            LOGGER.info("fromUserName:" + fromUserName + " toUserName:"+toUserName+" msgType:"+msgType+" event:"+event);

            if ("subscribe".equals(event)) {
                String createTime = reqMap.get("CreateTime");
                Map<String,Object> map = new HashMap<>();
                map.put("openid",fromUserName);
                map.put("createTime",createTime);
                map.put("bindingState","0");
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

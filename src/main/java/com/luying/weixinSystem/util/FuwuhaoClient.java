package com.luying.weixinSystem.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.luying.weixinSystem.util.http.HttpResult;
import com.luying.weixinSystem.util.http.HttpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class FuwuhaoClient {
    @Autowired
    HttpService httpService;

    /**
     * 根据服务号appid、secret获取accessToken
     * @param appid
     * @param secret
     * @return
     * @throws Exception
     */
    public String getAccessToken(String appid,String secret) throws Exception{
        String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+appid+"&secret="+secret;
        HttpResult result = httpService.doGet(url);
        Map<String,String> map = (Map<String, String>) JSONObject.parse(result.getBody());
        return map.get("access_token");
    }

    /**
     * 根据服务号appid、secret、code获取openid
     * @param appid
     * @param secret
     * @param code
     * @return
     * @throws Exception
     */
    public String getOpenid(String appid,String secret,String code) throws Exception{
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid="+appid+"&secret="+secret+"&code="+code+"&grant_type=authorization_code";
        HttpResult result = httpService.doGet(url);
        Map<String,String> map = (Map<String, String>) JSON.parse(result.getBody());
        return map.get("openid");
    }

    /**
     * 根据openid / accessToken 获取用户信息
     * @param accessToken
     * @param openid
     * @return
     * @throws Exception
     */
    public Map<String,String> getUserInfoByOpenid(String accessToken,String openid) throws Exception{
        String url = "https://api.weixin.qq.com/cgi-bin/user/info?access_token="+accessToken+"&openid="+openid+"&lang=zh_CN";
        HttpResult result = httpService.doGet(url);
        return (Map<String, String>) JSONObject.parse(result.getBody());
    }

    /**
     * 群发消息
     * @param map
     * @return
     * @throws Exception
     */
    public String sendMessageToAll(Map<String,Object> map) throws Exception{
        String url = "https://api.weixin.qq.com/cgi-bin/message/mass/sendall?access_token=" + map.get("accessToken");
        HttpResult result = httpService.doPost(url,map);
        Map<String,Object> resultMap = (Map<String, Object>) JSONObject.parse(result.getBody());
        return  resultMap.get("errcode").toString();
    }
}

package com.luying.weixinSystem.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.luying.weixinSystem.util.http.HttpResult;
import com.luying.weixinSystem.util.http.HttpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class WeixinClient {
    @Autowired
    HttpService httpService;

    /***
     * 获取token
     * @param corpid 企业ID
     * @param corpsecret 对应应用的corpsecret（例如通讯录/自建A应用）
     * @return
     * @throws Exception
     */
    public String getToken(String corpid,String corpsecret) throws Exception{
        String getAddressTokenUrl = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid="+corpid+"&corpsecret="+corpsecret;
        Map<String,String> map = new HashMap<String, String>();
        HttpResult result = httpService.doGet(getAddressTokenUrl);
        Map<String,String> tokenMap = (Map<String, String>) JSON.parse(result.getBody());
        return tokenMap.get("access_token").toString();
    }

    /***
     * 获取通讯录列表
     * @param addressToken 通讯录token
     * @return
     * @throws Exception
     */
    public List<Map<String,String>> getAddressList(String addressToken) throws Exception{
        //获取通讯录列表
        String getAddressListUrl = "https://qyapi.weixin.qq.com/cgi-bin/user/simplelist?access_token="+addressToken+"&department_id=1&fetch_child=1";
        HttpResult result2 = httpService.doGet(getAddressListUrl);
        return (List<Map<String, String>>) JSON.parseObject(result2.getBody()).get("userlist");
    }

    /***
     * 上传临时素材到微信获取media_id(文件类型)
     * @param accessToken
     * @return
     */
    public JSONObject uploadTempFile(String accessToken, File file) throws IOException {
        //上传临时素材
        String uploadUrl = "https://qyapi.weixin.qq.com/cgi-bin/media/upload?access_token="+accessToken+"&type=file";
        String resultUp = httpService.httpRequest(uploadUrl,file);
        return JSON.parseObject(resultUp);
    }

    /***
     * 发送消息
     * @param accessToken
     * @param contentMap
     * @return
     * @throws Exception
     */
    public String sendMessage(String accessToken,Map<String,Object> contentMap) throws  Exception{
        String sendMesUrl = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token="+accessToken;
        HttpResult result =  httpService.doPost(sendMesUrl,contentMap);
        Map<String,String> resultMap = (Map<String, String>) JSON.parse(result.getBody());
        return String.valueOf(resultMap.get("errcode"));
    }

}

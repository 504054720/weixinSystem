package com.luying.weixinSystem.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.luying.weixinSystem.util.http.HttpResult;
import com.luying.weixinSystem.util.http.HttpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class WeixinClient {
    @Autowired
    HttpService httpService;

    /**
     *获取应用的jsapi_ticket
     * */
    public String getJsapiTicket(String token)  throws Exception {
        String  url="https://qyapi.weixin.qq.com/cgi-bin/ticket/get?access_token="+token+"&type=agent_config";
        HttpResult result = httpService.doGet(url);
        return JSON.parse(result.getBody()).toString();
    }

    /**
     *获取企业的jsapi_ticket
     * */
    public String getQYJsapiTicket(String token)  throws Exception {
        String  url="https://qyapi.weixin.qq.com/cgi-bin/get_jsapi_ticket?access_token="+token;
        HttpResult result = httpService.doGet(url);
        return JSON.parse(result.getBody()).toString();
    }




    /**
     *sha1的加密算法
     * */
    public   String SHA1(String decript) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.update(decript.getBytes());
            byte messageDigest[] = digest.digest();
            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            // 字节数组转换为 十六进制 数
            for (int i = 0; i < messageDigest.length; i++) {
                String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
                if (shaHex.length() < 2) {
                    hexString.append(0);
                }
                hexString.append(shaHex);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     *根据code获取成员信息
     * @param a token
     * @param b code
     * */
    public String getUIdByCode(String a,String b) throws Exception{
        String  url="https://qyapi.weixin.qq.com/cgi-bin/user/getuserinfo?access_token="+a+"&code="+b;
        HttpResult result = httpService.doGet(url);
        return JSON.parse(result.getBody()).toString();
    }

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
        //String getAddressListUrl = "https://qyapi.weixin.qq.com/cgi-bin/user/simplelist?access_token="+addressToken+"&department_id=1&fetch_child=1";
        String getAddressListUrl = "https://qyapi.weixin.qq.com/cgi-bin/user/list?access_token="+addressToken+"&department_id=1&fetch_child=1";
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

    /***
     * 根据token userid 获取用户详细信息
     * @param accessToken
     * @param userId
     * @return
     * @throws Exception
     */
    public String getUserInfo(String accessToken,String userId) throws  Exception{
        String url = "https://qyapi.weixin.qq.com/cgi-bin/user/get?access_token="+accessToken+"&userid="+ userId;
        JSONObject jsonObject = new JSONObject();
        HttpResult result = httpService.doGet(url);
        Map<String,String> resultMap = (Map<String, String>) JSON.parse(result.getBody());
        jsonObject.put("errcode",resultMap.get("errcode"));
        jsonObject.put("userid",resultMap.get("userid"));
        jsonObject.put("name",resultMap.get("name"));
        jsonObject.put("department",resultMap.get("department"));
        jsonObject.put("mobile",resultMap.get("mobile"));
        jsonObject.put("is_leader_in_dept",resultMap.get("is_leader_in_dept"));
        jsonObject.put("enable",resultMap.get("enable"));
        return jsonObject.toJSONString();
    }
}

package com.luying.weixinSystem.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

/**
 * Created by zhang on 2019/4/19.
 */
public class SignClient {

    public static void fmain(String[] args)  throws Exception {

        String j="{'ticket':'1212asasasa','errcode':0}";

        JSONObject obj=   (JSONObject)JSONObject.parse(j);
       String c= obj.get("ticket").toString();

System.out.print(c);
    }
}
